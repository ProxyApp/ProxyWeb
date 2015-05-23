package engine.service

import engine.domain.{User, UserId}
import engine.domain.json.{user => jUser}
import engine.io.storage.RestStorage
import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Await

trait UserService{
  //todo ccoffey update this to use a real storage interface & drop the var
  var store: RestStorage[User]

  val groupManager: GroupManager
  val contactManager: ContactManager
  val channelManager: ChannelManager


  def user(id: UserId): Option[User] =
   Await.result(store.readContext(User.userKey(id))(jUser), 10 seconds)
    .fold[Option[User]](e => {
     Logger.error(e)
     None
   }, u => Some(u))

}

