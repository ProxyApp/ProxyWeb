package proxy.controllers

import engine.domain.{Email, UserId, User}
import engine.io.storage.{FirebaseUserStorage, RestStorage}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

case class LoginAttempt(email: String, pw: String)

trait AuthenticationContext extends Controller {

  val store: RestStorage[User]

  val loginAttemptForm = Form (
    mapping(
      "email" -> nonEmptyText.verifying(_.contains("@")),
      "pw" -> nonEmptyText)(LoginAttempt.apply)(LoginAttempt.unapply)
  )

  def validate = Action { implicit request =>
    loginAttemptForm.bindFromRequest.fold(req => {
      BadRequest(views.html.login(loginAttemptForm, "Bad Username or Password"))
    }, {
      case userData@LoginAttempt("me@proxy.com", "pw") => {
        val newUser = User(
          UserId("TestUserId"),
          "Chris Coffey",
          Email(userData.email),
          Nil,
          Nil,
          Nil)

        //todo create a real mapping of user data to the page
        Redirect(routes.Application.home())
      }
      case _ => BadRequest(views.html.login(loginAttemptForm, "Bad Username or Password"))
    } )
  }

}

object Authentication extends AuthenticationContext {
  val store = FirebaseUserStorage
}
