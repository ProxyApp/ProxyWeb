package engine.domain

import play.api.libs.json._
import julienrf.variants.Variants

package object json{

  implicit val channelId= Json.format[ChannelId]
  implicit val webChannel = Json.format[WebChannel]
  implicit val identityChannel = Json.format[IdentityChannel]
  implicit val channel: Format[Channel] = Variants.format[Channel]("type")
  implicit val userId= Json.format[UserId]
  implicit val groupId = Json.format[GroupId]
  implicit val contact = Json.format[Contact]
  implicit val group = Json.format[Group]

  implicit val user = Json.format[User]


}
