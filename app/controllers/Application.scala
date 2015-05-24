package proxy.controllers

import engine.domain.{json => eJ, ChannelSection, UserId, User}
import Tokens.Cookies._
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

  def channels = Action {implicit request => {
    cookieRequest(request){ s =>
      val user = s.split("=")(1)
      Await.result(Users.svc.store.readContext(User.userKey(UserId(user)))(eJ.user), 10 seconds) match {
        case Right(usr) => {
//          val share = usr.channels.filter(_.channelSection == ChannelSection.Share)
//          val locate = usr.channels.filter(_.channelSection == ChannelSection.Locate)
//          val chat = usr.channels.filter(_.channelSection == ChannelSection.Chat)
//          val transact = usr.channels.filter(_.channelSection == ChannelSection.Transact)
//          val play = usr.channels.filter(_.channelSection == ChannelSection.Play)
//          val schedule = usr.channels.filter(_.channelSection == ChannelSection.Schedule)

//          views.html.main("This Is Proxy", usr)(views.html.channels.render(usr,
//            chat,
//            share,
//            locate,
//            schedule,
//            transact,
//            play))

          Ok(views.html.main("This Is Proxy", usr)(views.html.channels.render()))

        }
        case _ =>
          BadRequest("Unknown User")
      }
    }
  }}

  private def cookieRequest(request: Request[AnyContent])(f: String => Result) = {
   extractUserInfo(request.headers.toMap("Cookie"))
    .fold(BadRequest("Invalid Request"))(f)
  }

  private def extractUserInfo(ls: Seq[String]): Option[String] = {
    ls.find(_.contains(User_Information))
  }
}