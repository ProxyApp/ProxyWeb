package proxy.controllers

import java.util.UUID

import engine.domain.{ UserId, User}
import engine.domain.json._
import engine.io.storage.{FirebaseUserStorage, RestStorage}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.{Await}
import scala.concurrent.duration._

case class LoginAttempt(email: String, pw: String)
case class NewUser(email: String, pw: String, first: String, last: String)
case class UserPwLookup(email: String, pw: String, id: UserId)

trait AuthenticationContext extends Controller {

  val store: RestStorage[User]

  val loginAttemptForm = Form (
    mapping(
      "email" -> nonEmptyText.verifying(_.contains("@")),
      "pw" -> nonEmptyText)(LoginAttempt.apply)(LoginAttempt.unapply)
  )

  val createUserForm = Form(
    mapping(
      "email" -> nonEmptyText.verifying(_.contains("@")),
      "pw" -> nonEmptyText,
      "first" -> nonEmptyText,
      "last" -> nonEmptyText)(NewUser.apply)(NewUser.unapply)
  )

  def validate = Action { implicit request =>
    loginAttemptForm.bindFromRequest.fold(req => {
      BadRequest(views.html.login(loginAttemptForm, "Bad Username or Password"))
    }, {
      case userData@LoginAttempt("me@proxy.com", "pw") => {
        val newUser = User(
          UserId("TestUserId"),
          "Chris",  "Coffey",
          userData.email,
          Nil,
          Nil,
          Nil)

        //todo create a real mapping of user data to the page
        Redirect(routes.Application.home())
      }
      case _ => BadRequest(views.html.login(loginAttemptForm, "Bad Username or Password"))
    } )
  }

  def createNew = Action { implicit request =>
    createUserForm.bindFromRequest.fold(req => {
      BadRequest(views.html.login(loginAttemptForm, "Bad Username or Password"))
    }, {
      case NewUser(mail, pw, fst, lst) => {
        val newUser = User(UserId(UUID.randomUUID().toString), fst, lst, mail, Nil, Nil, Nil)
        val emailNamePwTuple = UserPwLookup(mail, pw, newUser.id)

        println(store.f(newUser))
        val res =  Await.result(store.writeContext(newUser), 60 seconds)

        res match {
          case Left(s) => BadRequest(views.html.login(loginAttemptForm, "Could not create User"))
          case Right(u) => Redirect(routes.Application.home())
        }

      }
    })
  }

  private def generateToken = "Test"
}

object Authentication extends AuthenticationContext {
  val store = FirebaseUserStorage
}
