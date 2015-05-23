package engine.domain

sealed trait Channel {
  val label: String
  val id: ChannelId
}

case class IdentityChannel(id: ChannelId, label: String) extends Channel
object IdentityChannel {
  def Default = IdentityChannel(ChannelId("identity"), "Delete Me")
}
case class WebChannel(id: ChannelId, label: String, url: String) extends Channel
object WebChannel {
  val Identity = WebChannel(ChannelId("Google"), "Google Search", "https://www.google.com/#q=share+your+proxy")
}

