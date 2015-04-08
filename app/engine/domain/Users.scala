package engine.domain


case class Email(value: String)
case class User(id: UserId,
                name: String,
                email: Email,
                channels: List[Channel],
                contacts: List[Contact],
                groups: List[Group]) extends Identifiable[UserId]
