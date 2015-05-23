package engine.domain



case class Contact(id: UserId,
                   label: String,
                   channels: List[Channel]) extends Identifiable[UserId]

object Contact {
  val Identity = Contact(UserId("Identity"), "Identity", IdentityChannel.Default :: Nil)
}
