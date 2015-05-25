package controllers.translators

import engine.service.ChannelManager
import engine.service.Protocol.{CreatePhoneChannel, CreateWebChannel, ChannelCmd}
import play.api.libs.json.JsValue
import proxy.controllers.json.{WireCreatePhoneChannel, WireCreateWebChannel}


object ChannelTranslator extends JsonTranslator[ChannelCmd] {

  override def translate(json: JsValue): Option[ChannelCmd] = {
    val t = (json \ "channelType").as[String]
    t match {
      case "web" =>
        json.validate[WireCreateWebChannel].map(wire =>
          CreateWebChannel(ChannelManager.nextId, wire.label, wire.url, wire.section)
        ).asOpt
      case "phone" =>
        json.validate[WireCreatePhoneChannel].map(wire =>
          CreatePhoneChannel(ChannelManager.nextId, wire.label, wire.number, wire.sms, wire.section)
        ).asOpt
    }
  }

}
