package proxyweb

import engine.domain._
import engine.service.GroupManager.RemoveGroup
import engine.service.Protocol._
import engine.service.{Protocol => P}
import org.scalacheck.Gen

object Generators {

  //note Identifiers
  def userId = Gen.uuid.map(u => UserId(u.toString))
  def channelId = Gen.uuid.map(u => ChannelId(u.toString))
  def groupId = Gen.uuid.map(u => GroupId(u.toString))


  //note Protocol Commands
  def genCreateGroup: Gen[P.CreateGroup] =
    groupId.map(id => P.CreateGroup(id.value, id))

  def genRemoveGroup =
    groupId.map(id => P.RemoveGroup(id))

  def genChannel =
    Gen.oneOf(genWebChannel, genWebChannel)

  def genWebChannel = for {
    n <- Gen.alphaStr
    url <- Gen.alphaStr
    id <- channelId
  } yield WebChannel(id, n, url)

  def genLsContact(n: Int) = Gen.listOfN(n, genContact)
  def genContact = for{
    id <- userId
    lbl <- Gen.alphaStr
    n <- Gen.choose(1, 5)
    cs <- Gen.listOfN(n, genChannel)
  } yield Contact(id, lbl, cs)

  def genGroup = for {
    id <- groupId
    lbl <- Gen.alphaStr
    m <- Gen.choose(1,7)
    cs <- Gen.listOfN(m, genChannel)
    n <- Gen.choose(1, 5)
    contacts <- genLsContact(n)
  } yield Group(id, lbl, Some(cs), Some(contacts))


  def genUser = for {
    id <- userId
    first <- Gen.alphaStr
    last <- Gen.alphaStr
    email <- Gen.alphaStr
    m <- Gen.choose(1, 5)
    channels <- Gen.listOfN(m, genChannel)
    n <- Gen.choose(1, 5)
    contacts <- genLsContact(n)
    grps <- Gen.listOfN(m, genGroup)
  } yield User(id, first, last, email, channels, contacts, grps)


  def genRemoveGroup(g: GroupId) = for{
    id <- groupId
    a <- Gen.oneOf(g, id)
  } yield P.RemoveGroup(a)

  def genAddChannel(u: User) = for {
    gid <- groupId
    cid <- channelId
    a <- Gen.oneOf(u.groups.head.id, gid)
    b <- Gen.oneOf(u.groups.head.channels.head.map(_.id), cid)
  } yield P.AddChannelToGroup(a, b)

  def genRemoveChannel(u: User) = for {
    gid <- groupId
    cid <- channelId
    a <- Gen.oneOf(u.groups.head.id, gid)
    b <- Gen.oneOf(u.groups.head.channels.head.id, cid)
  } yield P.RemoveChannelFromGroup(a, b)

  def genAddContact(u: User) = for {
    gid <- groupId
    cid <- userId
    a <- Gen.oneOf(u.groups.head.id, gid)
    b <- Gen.oneOf(u.groups.head.contacts.head.id, cid)
  } yield P.AddContactToGroup(a, b)

  def genRemoveContact(u: User) = for {
    gid <- groupId
    cid <- userId
    a <- Gen.oneOf(u.groups.head.id, gid)
    b <- Gen.oneOf(u.groups.head.contacts.head.id, cid)
  } yield P.RemoveContactFromGroup(a, b)

  def genGroupCmd(u: User): Gen[GroupCmd] =
    Gen.oneOf(genCreateGroup,
      genAddChannel(u),
      genAddContact(u),
      genRemoveContact(u),
      genRemoveChannel(u),
      genRemoveGroup(u.groups.head.id))

  def genCreateWebChannel(u: User) = for {
    id <- channelId
    l <- Gen.alphaStr
    url <- Gen.alphaStr
    n <- Gen.choose(0, u.channels.length -1)
    a <- Gen.oneOf(l, u.channels(n).label)
    b <- Gen.oneOf(l, User.webChannels(u).head.label)
  } yield CreateWebChannel(id, a, b)

  def genRemoveWebChannel(u: User) = for{
    id <- channelId
    a <- Gen.oneOf(id, User.webChannels(u).head.id)
  } yield RemoveWebChannel(a)

  def genCreateContact(u: User) = for{
    id <- userId
    a <- Gen.oneOf(id, u.contacts.head.id)
  } yield CreateContact(a)

  def genRemoveUContact(u: User) = for {
    id <- userId
    a <- Gen.oneOf(id, u.contacts.head.id)
  } yield RemoveContact(a)

}



object Util {
  def lineByLineCompare[T](a: T, b: T) = {
    val as = a.toString.split(",")
    val bs = b.toString.split(",")

    as.zipWithIndex.map(t => (t._2, t._1, bs(t._2))).filter(x => x._2 != x._3).toList
  }

}

