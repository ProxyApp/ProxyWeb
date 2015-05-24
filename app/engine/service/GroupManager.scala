package engine.service

import java.util.UUID

import engine.domain.Error._
import engine.domain._
import engine.service.GroupManager._
import engine.service.{Protocol => P}

trait GroupManager extends ServiceCore[InternalGroupCmd, P.GroupCmd] {



  def interpret(context: User, cmd: P.GroupCmd): Err[InternalGroupCmd] = {
    implicit def toFail(s: String): Err[InternalGroupCmd] = <~[InternalGroupCmd](s)
    implicit def toSucc(a: InternalGroupCmd): Err[InternalGroupCmd] = ~>[InternalGroupCmd](a)

    cmd match {
      case P.CreateGroup(lbl, id) => context.groups.filter(_.label == lbl) match {
        case Nil => CreateGroup(context, lbl, id)
        case _ => "Duplicate Group Name. Names must be unique"
      }
      case P.RemoveGroup(g) => context.groups.exists(_.id == g) match {
        case true => context.groups.length match {
          case 1 => "Cannot Remove your last group for now!"
          case _ =>  RemoveGroup(context, context.groups.filter(_.id == g).head)
        }
        case false => "Unknown Group cannot be removed"
      }
      case P.AddChannelToGroup(id, c) =>
        context.channels.exists(_.id == c) && context.groups.exists(_.id == id) match {
        case true => {
          val g = context.groups.filter(_.id == id).head
          val channel = context.channels.filter(_.id == c).head
           AddChannelToGroup(context, g, channel)
        }
        case false => "Invalid group <-> channel association"
      }
      case P.RemoveChannelFromGroup(id, c) =>
        context.channels.exists(_.id == c) && context.groups.exists(_.id == id) match {
          case true => {
            val g = context.groups.filter(_.id == id).head
            RemoveChannelFromGroup(context, g, c)
          }
          case false => "Invalid group <-> channel association"
        }
      case P.AddContactToGroup(id, c) =>
        context.contacts.exists(_.id == c) && context.groups.exists(_.id == id) match {
          case true => {
            val g = context.groups.filter(_.id == id).head
            val contact = context.contacts.filter(_.id == c).head
            AddContactToGroup(context, g, contact)
          }
          case false => "Unknown group or contact"
        }
      case P.RemoveContactFromGroup(id, c) =>
        context.contacts.exists(_.id == c) && context.groups.exists(_.id == id) match {
          case true => {
            val g = context.groups.filter(_.id == id).head
            RemoveContactFromGroup(context, g, c)
          }
          case false => "Unknown group or contact"
        }
    }
  }

  import GroupManager._
  def process(cmd: InternalGroupCmd): Err[User] = {
    cmd match {
      case CreateGroup(u, lbl, id) =>{
        val newG = Group(id,lbl, None, None)
        ~>(u.copy(groups = newG :: u.groups))
      }
      case RemoveGroup(u, grp) =>
        ~>(u.copy(groups = u.groups.filterNot(_ == grp)))
      case AddChannelToGroup(u, grp, c) => {
        val cs = grp.channels.getOrElse(Nil)
        val newG = grp.copy(channels =  Some(c :: cs))
        ~>(swapGroup(u, newG, grp))
      }
      case RemoveChannelFromGroup(u, grp, c) => {
        val ng = grp.copy(channels = grp.channels.map(_.filterNot(_.id == c)))
        ~>(swapGroup(u, ng, grp))
      }
      case AddContactToGroup(u, grp, c) => {
        val cs = grp.contacts.getOrElse(Nil)
        val ng = grp.copy(contacts = Some(c :: cs))
        ~>(swapGroup(u, ng, grp))
      }
      case RemoveContactFromGroup(u, grp, c) => {
        val ng = grp.copy(contacts = grp.contacts.map(_.filterNot(_.id == c)))
        ~>(swapGroup(u, ng, grp))
      }
    }
  }


}

object GroupManager {

  // Commands
  sealed trait InternalGroupCmd extends Context[User]
  case class CreateGroup(context: User, label: String, id: GroupId) extends InternalGroupCmd
  case class RemoveGroup(context: User, g: Group) extends InternalGroupCmd
  case class AddChannelToGroup(context: User, g: Group, c: Channel) extends InternalGroupCmd
  case class RemoveChannelFromGroup(context: User, g: Group, c: ChannelId) extends InternalGroupCmd
  case class AddContactToGroup(context: User, g: Group, c: Contact) extends InternalGroupCmd
  case class RemoveContactFromGroup(context: User, g: Group, c: UserId) extends InternalGroupCmd

  private def swapGroup(u: User, g: Group, og: Group): User =
    u.copy(groups = g :: u.groups.filterNot(_ == og))

  def nextId = GroupId(UUID.randomUUID.toString)

}
