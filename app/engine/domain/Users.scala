package engine.domain


case class User(id: UserId,
                first: String,
                last: String,
                email: String,
                channels: List[Channel],
                contacts: List[Contact],
                groups: List[Group]) extends Identifiable[UserId]

object User {

  val Identity = User(UserId(""), "", "", "", Nil, Nil, Nil)

  def webChannels(u: User): List[WebChannel] =
   u.channels.foldLeft[List[WebChannel]](Nil)((acc, i) =>
    i match {
      case x: WebChannel => x :: acc
      case _ => acc
    })

  def userKey(key: UserId) = Identity.copy(id = key)

}
