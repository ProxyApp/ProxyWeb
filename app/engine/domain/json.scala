package engine.domain

import play.api.libs.json._
import julienrf.variants.Variants

package object json{


  implicit val channelId= Json.format[ChannelId]
  implicit val channel = Variants.format[Channel]
  implicit val userId= Json.format[UserId]
  implicit val groupId = Json.format[GroupId]
  implicit val webChannel = Json.format[WebChannel]
  implicit val contact = Json.format[Contact]
  implicit val group = Json.format[Group]

  implicit val user = Json.format[User]


}
