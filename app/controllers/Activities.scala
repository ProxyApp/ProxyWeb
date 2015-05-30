package proxy.controllers

import engine.io.storage.{FirebaseUserQueryable, SimpleArgument, Statement}
import play.api.mvc._
import engine.domain.json._

trait ActivitiesContext extends Controller{

  def search(name: Option[String], email: Option[String]) = Action {
    NotFound
  }

}

object Activities extends ActivitiesContext {


    def usersSearch = Action { req =>
        import engine.io.storage.FirebaseUserQueryable._
        val qs = req.queryString
        val query = Statement(order_by, eq, last)

        FirebaseUserQueryable.query(query).map{
            case None => BadRequest("Error")
            case Some(u) =>
        }



    }

}

