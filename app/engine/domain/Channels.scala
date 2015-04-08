package engine.domain

sealed trait Channel {
  val label: String
  val id: ChannelId
}

case class WebChannel(id: ChannelId, label: String, url: String) extends Channel
