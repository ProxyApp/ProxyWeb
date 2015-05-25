package engine.domain


object ChannelSection {
  val Share = "Share"
  val Chat = "Chat"
  val Transact = "Transact"
  val Locate = "Locate"
  val Schedule = "Schedule"
  val Play = "Play"
  val General = "General"

  lazy val sections = Share :: Chat :: Transact :: Locate :: Schedule :: Play :: General :: Nil

}

sealed trait Channel {
  val label: String
  val id: ChannelId
  val imageUrl: String
  val channelSection: String
}
object Channel {
  def href(c: Channel): String = c match {
    case IdentityChannel(_,_,_,_) => "https://shareyourproxy.com"
    case WebChannel(_, _, url, _, _) => url
    case PhoneChannel(_, _, n, _, _) => s"tel:${n}"
    case SmsChannel(_, _, n, _, _) => s"sms:${n}"
  }
}

case class IdentityChannel(id: ChannelId,
                           label: String,
                           channelSection: String,
                           imageUrl: String = IdentityChannel.Image) extends Channel
object IdentityChannel {
  def Default = IdentityChannel(ChannelId("identity"), "Delete Me", ChannelSection.Share)
  val Image = "http://static1.squarespace.com/static/5519a783e4b06e04b6a139d6/t/5522abcee4b03769e010bafa/1431534690289/?format=1500w"

}
case class WebChannel(id: ChannelId,
                      label: String,
                      url: String,
                      channelSection: String,
                      imageUrl: String = WebChannel.Image) extends Channel
object WebChannel {
  val Identity = WebChannel(ChannelId("Google"), "Google Search", "https://www.google.com/#q=share+your+proxy", ChannelSection.Share)
  val Image = "http://icons.iconarchive.com/icons/martz90/circle-addon2/256/browser-icon.png"
}

case class PhoneChannel(id: ChannelId,
                         label: String,
                         number: String,
                         channelSection: String,
                         imageUrl: String = PhoneChannel.Image) extends Channel
object PhoneChannel {
  val Image = "http://images.clipartpanda.com/phone-call-icon-aiqeMor9T.png"
}

case class SmsChannel(id: ChannelId,
                       label: String,
                       number: String,
                       channelSection: String,
                       imageUrl: String = SmsChannel.Image) extends Channel
object SmsChannel {
  val Image = "http://static1.squarespace.com/static/5519a783e4b06e04b6a139d6/553a6a9de4b04af6cfbad2c0/553a6a9ee4b085dcc8c3c17a/1429891746316/prototype_assets-11.png"
}


