package engine.domain

sealed trait Channel {
  val label: String
}

case class WebChannel(label: String, url: String) extends Channel
