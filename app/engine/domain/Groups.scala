package engine.domain


case class Group(id: GroupId,
                 label: String,
                 channels: List[Channel],
                 contacts: List[Contact]) extends Identifiable[GroupId]
