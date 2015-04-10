package engine.service

import engine.domain.Error._
import engine.domain._
import engine.service.ContactManager.{RemoveContact, AddContact, InternalContactCmd}
import engine.service.{Protocol => P}
import engine.domain.{UserId, User}

trait ContactManager {

  def contact(id: UserId): Option[Contact]

  def interpret(context: User, cmd: P.ContactCmd): Err[InternalContactCmd] = {
    implicit def toFail(s: String): Err[InternalContactCmd] = <~[InternalContactCmd](s)
    implicit def toSucc(a: InternalContactCmd): Err[InternalContactCmd] = ~>[InternalContactCmd](a)

    cmd match {
      case P.CreateContact(id) => context.contacts.exists(_.id == id) match {
        case false =>
          contact(id).fold[Err[InternalContactCmd]]("Unknown Contact")(c => AddContact(context, c))
        case true  => "Contact has already been added."
      }
      case P.RemoveContact(id) => context.contacts.exists(_.id  == id) match {
        case true => RemoveContact(context, id)
        case false => "Unknown Contact."
      }
    }
  }

  def process(cmd: InternalContactCmd): Err[User] = {
    cmd match {
      case AddContact(u, c) =>
        ~>(u.copy(contacts = c :: u.contacts))
      case RemoveContact(u, id) =>
        ~>(u.copy(contacts = u.contacts.filterNot(_.id == id)))
    }
  }

}

object ContactManager{
  trait InternalContactCmd extends Context[User]

  case class AddContact(context: User, c: Contact) extends InternalContactCmd
  case class RemoveContact(context: User, id: UserId) extends InternalContactCmd

}