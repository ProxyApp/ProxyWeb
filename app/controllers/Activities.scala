package controllers

import play.api.mvc._

trait Activities extends Controller{

  def search(name: Option[String], email: Option[String]) = Action {
    NotFound
  }

}
