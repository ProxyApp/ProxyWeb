package engine.service

import engine.domain.{User, UserId}
import engine.domain.json.{user => jUser}
import engine.domain.json
import engine.io.storage.RestStorage
import scala.concurrent.ExecutionContext.Implicits.global

trait UserService{
  //todo ccoffey update this to use a real storage interface & drop the var
  var store: RestStorage[User]

  val groupManager: GroupManager
  val contactManager: ContactManager
  val channelManager: ChannelManager


  def user(id: UserId): Option[User] =
    store.readContext(User.userKey(id))(jUser).map {
      case Left(e) => None
      case Right(u) => Some(u)
    }.value.fold[Option[User]](None)(t => t.toOption.flatMap(x => x))

}

