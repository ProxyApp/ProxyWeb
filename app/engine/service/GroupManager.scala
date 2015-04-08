package engine.service

import java.util.UUID

import engine.domain.Error._
import engine.domain._
import engine.service.GroupManager._
import engine.service.{Protocol => P}

trait GroupManager extends ServiceCore[InternalGroupCmd, P.GroupCmd] {

  def interpret(context: User, cmd: P.GroupCmd): Err[InternalGroupCmd] = {
    cmd match {
      case P.CreateGroup(lbl) => context.groups.filter(_.label == lbl) match {
        case Nil => ~>[InternalGroupCmd](CreateGroup(context, lbl))
        case _ => <~[InternalGroupCmd]("Duplicate Group Name. Names must be unique")
      }
      case P.RemoveGroup(g) => context.groups.exists(_.id == g) match {
        case true => ~>[InternalGroupCmd](RemoveGroup(context, context.groups.filter(_.id == g).head))
        case false => <~[InternalGroupCmd]("Unknown Group cannot be removed")
      }
      case P.AddChannelToGroup(id, c) =>
        context.channels.exists(_.id == c) && context.groups.exists(_.id == id) match {
        case true => {
          val g = context.groups.filter(_.id == id).head
          val channel = context.channels.filter(_.id == c).head
           ~>[InternalGroupCmd](AddChannelToGroup(context, g, channel))
        }
        case false => <~[InternalGroupCmd]("Invalid group <-> channel association")
      }
      case P.RemoveChannelFromGroup(id, c) =>
        context.channels.exists(_.id == c) && context.groups.exists(_.id == id) match {
          case true => {
            val g = context.groups.filter(_.id == id).head
            ~>[InternalGroupCmd](RemoveChannelFromGroup(context, g, c))
          }
          case false => <~[InternalGroupCmd]("Invalid group <-> channel association")
        }
      case P.AddContactToGroup(id, c) =>
        context.contacts.exists(_.id == c) && context.groups.exists(_.id == id) match {
          case true => {
            val g = context.groups.filter(_.id == id).head
            val contact = context.contacts.filter(_.id == c).head
            ~>[InternalGroupCmd](AddContactToGroup(context, g, contact))
          }
          case false => <~[InternalGroupCmd]("Unknown group or contact")
        }
      case P.RemoveContactFromGroup(id, c) =>
        context.contacts.exists(_.id == c) && context.groups.exists(_.id == id) match {
          case true => {
            val g = context.groups.filter(_.id == id).head
            ~>[InternalGroupCmd](RemoveContactFromGroup(context, g, c))
          }
          case false => <~[InternalGroupCmd]("Unknown group or contact")
        }
    }
  }

  import GroupManager._
  def process(cmd: InternalGroupCmd): Err[User] = {
    cmd match {
      case CreateGroup(u, lbl) =>{
        val newG = Group(newId(), lbl, Nil, Nil)
        ~>(u.copy(groups = newG :: u.groups))
      }
      case RemoveGroup(u, grp) =>
        ~>(u.copy(groups = u.groups.filterNot(_ == grp)))
      case AddChannelToGroup(u, grp, c) => {
        val newG = grp.copy(channels =  c :: grp.channels)
        ~>(swapGroup(u, newG, grp))
      }
      case RemoveChannelFromGroup(u, grp, c) => {
        val ng = grp.copy(channels = grp.channels.filterNot(_.id == c))
        ~>(swapGroup(u, ng, grp))
      }
      case AddContactToGroup(u, grp, c) => {
        val ng = grp.copy(contacts = c :: grp.contacts)
        ~>(swapGroup(u, ng, grp))
      }
      case RemoveContactFromGroup(u, grp, c) => {
        val ng = grp.copy(contacts = grp.contacts.filterNot(_.id == c))
        ~>(swapGroup(u, ng, grp))
      }
    }
  }


}


trait Context[T] {
  val context: T
}


object GroupManager {

  // Commands
  sealed trait InternalGroupCmd extends Context[User]
  case class CreateGroup(context: User, label: String) extends InternalGroupCmd
  case class RemoveGroup(context: User, g: Group) extends InternalGroupCmd
  case class AddChannelToGroup(context: User, g: Group, c: Channel) extends InternalGroupCmd
  case class RemoveChannelFromGroup(context: User, g: Group, c: ChannelId) extends InternalGroupCmd
  case class AddContactToGroup(context: User, g: Group, c: Contact) extends InternalGroupCmd
  case class RemoveContactFromGroup(context: User, g: Group, c: UserId) extends InternalGroupCmd


  def newId() = GroupId(UUID.randomUUID().toString)

  private def swapGroup(u: User, g: Group, og: Group): User =
    u.copy(groups = g :: u.groups.filterNot(_ == og))

}
