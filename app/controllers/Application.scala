package proxy.controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def login = Action {
    Ok(views.html.login(Authentication.loginAttemptForm))
  }

  def home = Action {
    Ok(views.html.main("This Is Proxy")(views.html.groups.render()))
  }

}