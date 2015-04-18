package engine.service

import engine.domain.{User, UserId}

trait UserService{
  //todo ccoffey update this to use a real storage interface & drop the var
  var store: Map[UserId, User]

  val groupManager: GroupManager
  val contactManager: ContactManager
  val channelManager: ChannelManager


  def allUsers: List[User] = store.values.toList
  def user(id: UserId): Option[User] =
    store.get(id)

}

