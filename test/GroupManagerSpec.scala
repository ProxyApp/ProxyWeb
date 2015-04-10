package engine.service

import engine.domain.User
import engine.service.Protocol.AddChannelToGroup
import org.scalacheck.Properties
import org.scalacheck.Prop._
import proxyweb.Generators._
import engine.service.{Protocol => P}
import proxyweb.Util

object GroupManagerSpec extends Properties("Group Manager"){

  case object GrpManager extends GroupManager



  property("Group Names must be unique") = forAll(genUser, groupId)((u, g) =>{
    val createGrp = P.CreateGroup("Test", g)

    (for{
      a <- GrpManager.interpret(u, createGrp)
      b <- GrpManager.process(a)
      c <- GrpManager.interpret(b, createGrp)
    } yield c)
      .fold(_ => true, _ => false)

  })

  property("May only remove a known group") =
    forAll(genUser, groupId.flatMap(genRemoveGroup(_)))((u, rg) => {
      (for{
      a <- GrpManager.interpret(u, rg)
      b <- GrpManager.process(a)
    } yield a)
      .fold(_ => !u.groups.exists(_.id == rg.id), _ => u.groups.exists(_.id == rg.id))
  })

  property("May only add channel to a group if the group and channel exist") =
    forAll(genUser.map(u => (u, genAddChannel(u).sample.head)))( (t: (User, AddChannelToGroup)) => {
      val (u, c) = t
      (for {
        a <- GrpManager.interpret(u, c)
        b <- GrpManager.process(a)
      }yield a)
      .fold(_ =>
        !u.channels.exists(_.id == c.channel) || !u.groups.exists(_.id == c.id),
        _ => u.channels.exists(_.id == c.channel) && u.groups.exists(_.id == c.id))
    })

  property("May only remove a channel from a group if the group contains the channel") =
    forAll(genUser.map(u => (u, genRemoveChannel(u).sample.head)))(t => {
      val (u, c) = t
      (for{
        a <- GrpManager.interpret(u, c)
        b <- GrpManager.process(a)
      }yield a)
      .fold( _ =>
          !u.channels.exists(_.id == c.channel) ||
            !u.groups.exists(g => g.id == c.id && g.channels.exists(_.id == c.channel)),
      _ => u.channels.exists(_.id == c.channel) &&
        u.groups.exists(g => g.id == c.id && g.channels.exists(_.id == c.channel)))
    })

  property("May only add a contact to a group if both group and contact exist") =
    forAll(genUser.map(u => (u, genAddContact(u).sample.head)))(t => {
      val (u, c) = t
      (for{
        a <- GrpManager.interpret(u, c)
        b <- GrpManager.process(a)
      }yield a)
        .fold( _ =>
        !u.contacts.exists(_.id == c.contactId) || !u.groups.exists(_.id == c.id),
          _ => u.contacts.exists(_.id == c.contactId) && u.groups.exists(_.id == c.id))
    })

  property("May only remove a contact to if both group and contact exist") =
    forAll(genUser.map(u => (u, genAddContact(u).sample.head)))(t => {
      val (u, c) = t
      (for{
        a <- GrpManager.interpret(u, c)
        b <- GrpManager.process(a)
      }yield a)
        .fold( _ =>
        !u.contacts.exists(_.id == c.contactId) ||
          !u.groups.exists(g => g.id == c.id && g.contacts.exists(_.id == c.contactId)),
          _ => u.contacts.exists(_.id == c.contactId) &&
            u.groups.exists(g => g.id == c.id && g.contacts.exists(_.id == c.contactId)))
    })


  property("Command handling is idempotent") =
    forAll(genUser.map(u => (u, genGroupCmd(u).sample.head)))(t => {
    val (u, cmd) = t
    (for{
      a <- GrpManager.interpret(u, cmd)
      b <- GrpManager.process(a)
      c <- GrpManager.interpret(u, cmd)
      d <- GrpManager.process(c)
    } yield b == d) //note right now only ~1/2 of cases are legitimate
      .fold(_ => true, x => x)
  })





}
