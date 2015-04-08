package proxyweb

import engine.domain._
import engine.service.GroupManager.RemoveGroup
import engine.service.{Protocol => P}
import org.scalacheck.Gen

object Generators {

  //note Identifiers
  def userId = Gen.uuid.map(u => UserId(u.toString))
  def channelId = Gen.uuid.map(u => ChannelId(u.toString))
  def groupId = Gen.uuid.map(u => GroupId(u.toString))


  //note Protocol Commands
  def genCreateGroup: Gen[P.CreateGroup] =
    groupId.map(id => P.CreateGroup(id.value))

  def genRemoveGroup =
    groupId.map(id => P.RemoveGroup(id))

  def genChannel =
    Gen.oneOf(genWebChannel, genWebChannel)

  def genLsWebChannel = Gen.listOf(genWebChannel)
  def genWebChannel = for {
    n <- Gen.alphaStr
    url <- Gen.alphaStr
    id <- channelId
  } yield WebChannel(id, n, url)

  def genLsContact(n: Int) = Gen.listOfN(n, genContact)
  def genContact = for{
    id <- userId
    lbl <- Gen.alphaStr
    n <- Gen.choose(1, 20)
    cs <- Gen.listOfN(n, genChannel)
  } yield Contact(id, lbl, cs)

  def genGroup = for {
    id <- groupId
    lbl <- Gen.alphaStr
    cs <- Gen.listOf(genChannel)
    n <- Gen.choose(0, 50)
    contacts <- genLsContact(n)
  } yield Group(id, lbl, cs, contacts)


  def genUser = for {
    id <- userId
    name <- Gen.alphaStr
    email <- Gen.alphaStr.map(Email(_))
    channels <- Gen.listOf(genChannel)
    n <- Gen.choose(0, 50)
    contacts <- genLsContact(n)
    grps <- Gen.listOf(genGroup)
  } yield User(id, name, email, channels, contacts, grps)


  def genRemoveGroup(g: GroupId) = for{
    id <- groupId
    a <- Gen.oneOf(g, id)
  } yield P.RemoveGroup(a)

  def genAddChannel(u: User) = for {
    gid <- groupId
    cid <- channelId
    a <- Gen.oneOf(u.groups.head.id, gid)
    b <- Gen.oneOf(u.channels.head.id, cid)
  } yield P.AddChannelToGroup(a, b)
}


