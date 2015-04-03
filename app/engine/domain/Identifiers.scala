package engine.domain

trait Id {
  val value: String
}

case class ChannelId(value: String) extends Id
case class UserId(value: String) extends Id
case class GroupId(value: String) extends Id

trait Identifiable[I <: Id]{
  val id: I
}
