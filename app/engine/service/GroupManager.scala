package engine.service

import engine.domain.{UserId, ChannelId, GroupId}

object GroupManager {

  sealed trait GroupCmd
  case class CreateGroup(label: String) extends GroupCmd
  case class RemoveGroup(id: GroupId) extends GroupCmd
  case class AddChannelToGroup(id: GroupId, channel: ChannelId) extends GroupCmd
  case class AddContactToGroup(id: GroupId, contactId: UserId) extends GroupCmd


  def interpret(cmd: GroupCmd):

}
