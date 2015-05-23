package proxy.controllers

import engine.domain.{json => eJ}
import Tokens.Cookies._
import engine.domain.{UserId, User}
import play.api._
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.duration._

object Application extends Controller {

  def login = Action {
    Ok(views.html.login(Authentication.loginAttemptForm))
  }

  def home = Action { implicit request => {
    request.headers.toMap("Cookie").find(_.contains(User_Information))
      .fold(BadRequest("Invalid Request"))(c => {
      val user = c.split("=")(1)
      Await.result(Users.svc.store.readContext(User.userKey(UserId(user)))(eJ.user), 10 seconds) match {
         case Right(usr) =>
           Logger.debug(s"Home navigation for user ${usr.email}[id=${usr.id.value}}]")
          Ok(views.html.main("This Is Proxy", usr)(views.html.groups.render(usr)))
        case _ =>
          BadRequest("Unknown User")
      }
    })
  }}

  def createUser = Action {
    Ok(views.html.createUser(Authentication.createUserForm))
  }

}