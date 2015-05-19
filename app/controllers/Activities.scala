package proxy.controllers

import play.api.mvc._

trait ActivitiesContext extends Controller{

  def search(name: Option[String], email: Option[String]) = Action {
    NotFound
  }

}

object Activities extends ActivitiesContext {
  //todo wire up everything in here
}
