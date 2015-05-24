package engine.io.storage

import engine.io.storage.Rest.Path
import play.api.Logger
import play.api.libs.json.{Json, Reads, Writes}
import play.api.libs.ws.WSResponse
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

import scala.concurrent.Future

trait Rest {
  import Rest.Path

  def put[T](path: Path, body: T, secure: Boolean = true)(implicit writes: Writes[T]): Future[WSResponse]

  def get[T](path: Path, secure: Boolean = true)(implicit reads: Reads[T]): Future[WSResponse]

  def delete(path: Path, secure: Boolean = true): Future[WSResponse]

  //Implenent Rest for Firebase



}

object Rest {
  type Path = List[String]

  def path(root: String, obj: String *) : Path =
    root :: obj.toList
}


trait RestStorage[A] {
  val client: Rest
  val f: A => Path //this isn't a great signature, but its good enough

  def readContext(key: A)(implicit reads: Reads[A]): Future[Either[String, A]] =
    client.get(f(key)).map(handleResponse(_, (x) => {
      val j = Json.parse(x)
      val a = j.validate[A]
      a.asOpt
    }))

  def writeContext(data: A)(implicit writes: Writes[A]): Future[Either[String, A]] =
    client.put(f(data), writes.writes(data)).map(handleResponse(_, x => Some(data)))

  def removeContext(data: A): Future[Either[String, A]] =
    client.delete(f(data)).map(handleResponse(_, (x) => Some(data)))

  private def handleResponse(r: WSResponse, g: String => Option[A]) =
    r.status match {
      case x if x >= 200 && x < 300 => g(r.body)
        .fold[Either[String, A]](Left("Could not process Json Response"))(a => Right(a))
      case x if x >= 400 => {
        Logger.error(r.statusText)
        Left(r.statusText)
      }
      case _ => Left(r.statusText)
    }
}


