package engine.service

import org.scalacheck.Properties
import org.scalacheck.Prop._
import proxyweb.Generators._
import engine.domain.User

object ChannelManagerSpec extends Properties("Channel Manager"){

  object CM extends ChannelManager

  property("May only create web channels with unique label and url") =
    forAll(genUser.map(u => (u, genCreateWebChannel(u).sample.head)))(u => {
      val (usr, cmd) = u

      (for{
        a <- CM.interpret(usr, cmd)
        b <- CM.process(a)
      } yield b)
        .fold(_ => User.webChannels(usr).exists(c => c.label == cmd.label || c.url == cmd.url),
          x => !User.webChannels(usr).exists(c => c.label == cmd.label || c.url == cmd.url)
        )
    })

  property("May only remove existing channels") =
    forAll(genUser.map(u => (u, genRemoveWebChannel(u).sample.head)))(u => {
      val (usr, cmd) = u

      (for{
        a <- CM.interpret(usr, cmd)
        b <- CM.process(a)
      }yield b)
        .fold(_ => !User.webChannels(usr).exists(_.id == cmd.id),
          x => User.webChannels(usr).exists(_.id == cmd.id))
    })


}
