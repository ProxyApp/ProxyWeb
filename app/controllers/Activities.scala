package proxy.controllers

import engine.io.storage.{FirebaseUserQueryable, SimpleArgument, Statement}
import play.api.libs.json.Json
import play.api.mvc._
import engine.domain.json._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

trait ActivitiesContext extends Controller{

  def search(name: Option[String], email: Option[String]) = Action {
    NotFound
  }

    def usersSearch = Action { req =>
        import engine.io.storage.FirebaseUserQueryable._
        val qs = req.queryString

        //note this can be so much cleaner
        val f =  FirebaseUserQueryable.query(Seq((order_by.asString, last.asString)))(engine.domain.json.userSeqReads).map{
            case None => BadRequest("Error")
            case Some(u) => Ok(Json.arr(u.map(userSearchWrites.writes)))
        }
        Await.result(f, 1 minute)
    }

}

object Activities extends ActivitiesContext {




}

