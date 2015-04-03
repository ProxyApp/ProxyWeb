package engine.domain

import com.sksamuel.scrimage.Image


case class Email(value: String)
case class User(id: UserId,
                name: String,
                email: Email,
                image: Image,
                channels: List[Channel],
                contacts: List[Contact],
                groups: List[Group]) extends Identifiable[UserId]
