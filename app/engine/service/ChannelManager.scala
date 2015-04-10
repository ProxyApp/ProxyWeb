package engine.service

import engine.domain.Error._
import engine.domain._
import engine.service.ChannelManager.{RemoveWebChannel, CreateWebChannel, InternalChannelCmd}
import engine.service.{Protocol => P}

trait ChannelManager extends ServiceCore[InternalChannelCmd, P.ChannelCmd]  {

  def interpret(context: User, cmd: P.ChannelCmd): Err[InternalChannelCmd] = {
    implicit def toFail(s: String): Err[InternalChannelCmd] = <~[InternalChannelCmd](s)
    implicit def toSucc(a: InternalChannelCmd): Err[InternalChannelCmd] = ~>[InternalChannelCmd](a)

    cmd match {
      case P.CreateWebChannel(id, lbl, url) =>
        context.channels.filter {
          case WebChannel(_, l, u) => l == lbl || u == url
          case _ => false
        } match {
          case Nil => CreateWebChannel(context, id, lbl, url)
          case _ => "Label and URL must be unique."
        }
      case P.RemoveWebChannel(id) => User.webChannels(context).filter(_.id == id) match {
        case h :: x => RemoveWebChannel(context, id)
        case Nil => "Unknown channel."
      }
    }
  }

  def process(cmd: InternalChannelCmd): Err[User] = {
    cmd match {
      case CreateWebChannel(u, id, lbl, url) =>
        ~>(u.copy(channels = WebChannel(id, lbl, url) :: u.channels))
      case RemoveWebChannel(u, id) =>
        ~>(u.copy(channels = u.channels.filterNot(_.id == id)))
    }
  }

}

object ChannelManager {

  sealed trait InternalChannelCmd extends Context[User]
  case class CreateWebChannel(context: User,
                              id: ChannelId,
                              label: String,
                              url: String) extends InternalChannelCmd
  case class RemoveWebChannel(context: User, id: ChannelId) extends InternalChannelCmd

}
