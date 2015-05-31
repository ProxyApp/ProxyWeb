package engine.io.storage

import com.typesafe.config.ConfigFactory
import engine.domain.{SearchUser, User}
import engine.io.storage.Rest.Path
import play.api.libs.json.{Writes, Reads}
import play.api.libs.ws.{WSRequestHolder, WS, WSResponse}
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

import scala.concurrent.Future

object FirebaseClient extends Rest {
  override def put[T](path: Path, body: T, secure: Boolean)(implicit writes: Writes[T]): Future[WSResponse] ={
    firebasePath(path, secure).put(writes.writes(body))
  }

  override def get[T](path: Path, secure: Boolean)(implicit reads: Reads[T]): Future[WSResponse] =
    firebasePath(path, secure).get

  override def delete(path: Path, secure: Boolean): Future[WSResponse] =
    firebasePath(path, secure).delete

  private def firebasePath(path: Path, secure: Boolean): WSRequestHolder = {
    val url =  s"http${if (secure) "s"}://${path.mkString("", "/", ".json")}"
    println(url)
    WS.url(url)
  }
}

object FirebaseUserStorage extends RestStorage[User] {
  private lazy val root = ConfigFactory.load().getString("Firebase.Root")
  val client = FirebaseClient
  val f = (user: User) => {
    Rest.path(root, "webusers", user.id.value)
  }
}

object FirebaseUserQueryable extends RestQueryable[Seq[SearchUser]] {
    def get(p: Path) =
        WS.url(s"https://${location.mkString("/")}?${p.mkString("")}")
    val location: Path =
        ConfigFactory.load().getString("Firebase.Root") ::
        "webusers.json" ::
        Nil

    val order_by = SimpleArgument("orderBy")
    val equalsVaue = StringOperator("=")
    val first = SimpleArgument("first")
    val last = SimpleArgument("\"last\"")
}

