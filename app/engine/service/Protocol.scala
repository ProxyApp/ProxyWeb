package engine.service

import engine.domain.{ChannelId, GroupId, UserId}

object Protocol {

  /**
   * This is a marker trait for modifications to the user's context.
   */
  sealed trait UserContextCmd


  /**
   * For a given group, you may perform the following actions:
   *  - Create it
   *  - Delete it
   *  - Add a user to the group
   *  - Remove the user for the group
   *  - Add one of your channels to the group
   *  - Remove one of your channels from the group
   */
  sealed trait GroupCmd extends UserContextCmd
  case class CreateGroup(label: String) extends GroupCmd
  case class RemoveGroup(id: GroupId) extends GroupCmd
  case class AddChannelToGroup(id: GroupId, channel: ChannelId) extends GroupCmd
  case class RemoveChannelFromGroup(id: GroupId, channel: ChannelId) extends GroupCmd
  case class AddContactToGroup(id: GroupId, contactId: UserId) extends GroupCmd
  case class RemoveContactFromGroup(id: GroupId, contactId: UserId) extends GroupCmd


  /**
   * Activities are things which do not modify the user context
   */
  sealed trait ActivityCmd
  case class FindContact(name: Option[String], email: Option[String]) extends ActivityCmd


}
