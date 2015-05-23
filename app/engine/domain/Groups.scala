package engine.domain


case class Group(id: GroupId,
                 label: String,
                 channels: List[Channel],
                 contacts: List[Contact]) extends Identifiable[GroupId]
object Group{
  val Identity = Group(GroupId("Identity"), "Default", IdentityChannel.Default :: Nil, Contact.Identity :: Nil)
}
