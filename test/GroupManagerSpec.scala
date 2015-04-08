package engine.service

import engine.domain.User
import engine.service.Protocol.AddChannelToGroup
import org.scalacheck.Properties
import org.scalacheck.Prop._
import proxyweb.Generators._
import engine.service.{Protocol => P}

object GroupManagerSpec extends Properties("Group Manager"){

  case object GrpManager extends GroupManager



  property("Group Names must be unique") = forAll(genUser)(u =>{
    val createGrp = P.CreateGroup("Test")

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

  property("Command handling is idempotent") = forAll(genUser)(u => {
    val createGrp = P.CreateGroup("abce")
    (for{
      a <- GrpManager.interpret(u, createGrp)
      b <- GrpManager.process(a)
      c <- GrpManager.interpret(u, createGrp)
      d <- GrpManager.process(c)
    } yield b == d)
      .fold(_ => false, x => x)
  })




}
