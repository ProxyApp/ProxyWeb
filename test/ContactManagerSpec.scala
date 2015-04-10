package engine.service

import engine.domain.{Contact, UserId}
import org.scalacheck.Prop._
import org.scalacheck.Properties
import proxyweb.Generators._

object ContactManagerSpec extends Properties("Contact Manager"){

  object CM extends ContactManager{
 //todo ccoffey create a more legitimate implementation of this
    def contact(id: UserId): Option[Contact] = {
      if(id.hashCode() < 0) None
      else Some(Contact(id, id.value.toString, Nil))
    }
  }

  property("May only add an existing contact")=
    forAll(genUser.map(u => (u, genCreateContact(u).sample.head)))(t => {
      val (u, c) = t

      (for{
        a <- CM.interpret(u, c)
        b <- CM.process(a)
      }yield b)
        .fold(_ => !CM.contact(c.id).isDefined || u.contacts.exists(_.id == c.id),
          x => CM.contact(c.id).isDefined && !u.contacts.exists(_.id == c.id))
    })

  property("May only remove a contact that has been added") =
    forAll(genUser.map(u => (u, genRemoveUContact(u).sample.head)))(t => {
      val (u, c) = t

      (for{
        a <- CM.interpret(u, c)
        b <- CM.process(a)
      }yield b)
        .fold(_ => !u.contacts.exists(_.id == c.id), x => u.contacts.exists(_.id == c.id))
    })

  property("Remove contact only removes the contact") =
    forAll(genUser.map(u => (u, genRemoveUContact(u).sample.head)))(t => {
      val (u, c) = t

      (for{
        a <- CM.interpret(u, c)
        b <- CM.process(a)
      }yield b)
        .fold(_ => !u.contacts.exists(_.id == c.id), x => u.contacts.filterNot(_.id == c.id) == x.contacts)
    })
}
