package object proxy {

  import java.net.URL

  trait Channel

  case class LinkChannel(address: URL) extends Channel

  case class Group(users: List[User], channels: List[Channel])

  case class User(name: String, groups: List[Group], contacts: List[Contact], channels: List[Channel])

  case class Contact(user: User, channels: List[Channel])

  type State[S, A] = (S, A)

}
