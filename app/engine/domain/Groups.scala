package engine.domain


case class Group(id: GroupId,
                 label: String,
                 channels: Option[List[Channel]],
                 contacts: Option[List[Contact]]) extends Identifiable[GroupId]
object Group{
  val Identity = Group(GroupId("Identity"), "Default", None, None)
}
