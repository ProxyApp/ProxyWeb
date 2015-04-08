package engine.domain



case class Contact(id: UserId,
                   label: String,
                   channels: List[Channel]) extends Identifiable[UserId]
