package controllers

import play.api.data.Form
import play.api.mvc._

trait Authentication extends Controller {

  def validate(email: String, pw: String): Boolean


}

object Authentication {

}
