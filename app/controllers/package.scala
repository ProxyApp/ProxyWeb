package proxy.controllers

import engine.domain._
import julienrf.variants.Variants
import play.api.http.Writeable
import play.api.libs.json.{JsValue, Json, Writes, Reads}
import play.api.mvc._

package object json {

  implicit def jsWriteable[A](implicit a: Writes[A], js: Writeable[JsValue]): Writeable[A] =
    js.map(b => Json.toJson(b))

  implicit val uidr = Json.reads[UserId]
  implicit val uidw = Json.writes[UserId]
  implicit val cidr = Json.reads[ChannelId]
  implicit val cidw = Json.writes[ChannelId]
  implicit val gidr = Json.reads[GroupId]
  implicit val gidw = Json.writes[GroupId]
  implicit val chanr = Variants.reads[Channel]
  implicit val chanw = Variants.writes[Channel]
  implicit val conr = Json.reads[Contact]
  implicit val conw = Json.writes[Contact]
  implicit val gr = Json.reads[Group]
  implicit val gw = Json.writes[Group]
  implicit val ur = Json.reads[User]
  implicit val uw = Json.writes[User]

  case class WireCreateWebChannel(label: String, url: String)
  object WireCreateWebChannel {
    implicit val r = Json.reads[WireCreateWebChannel]
    implicit val w = Json.writes[WireCreateWebChannel]
  }

  case class WireCreateGroup(label: String)
  object WireCreateGroup {
    implicit val r = Json.reads[WireCreateGroup]
    implicit val w = Json.writes[WireCreateGroup]
  }

  case class WireAddGroupContact(contact_id: String)
  object WireAddGroupContact {
    implicit val r = Json.reads[WireAddGroupContact]
    implicit val w = Json.writes[WireAddGroupContact]
  }

  case class WireAddGroupChannel(channel_id: String)
  object WireAddGroupChannel {
    implicit val r = Json.reads[WireAddGroupChannel]
    implicit val w = Json.writes[WireAddGroupChannel]
  }


}
