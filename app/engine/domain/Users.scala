package engine.domain


case class Email(value: String)
case class User(id: UserId,
                name: String,
                email: Email,
                channels: List[Channel],
                contacts: List[Contact],
                groups: List[Group]) extends Identifiable[UserId]

object User {

  def webChannels(u: User): List[WebChannel] =
   u.channels.foldLeft[List[WebChannel]](Nil)((acc, i) =>
    i match {
      case x: WebChannel => x :: acc
      case _ => acc
    })

}
