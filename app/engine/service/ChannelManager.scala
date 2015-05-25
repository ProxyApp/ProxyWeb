package engine.service

import java.util.UUID

import engine.domain.Error._
import engine.domain._
import engine.service.ChannelManager._
import engine.service.{Protocol => P}

trait ChannelManager extends ServiceCore[InternalChannelCmd, P.ChannelCmd]  {

  def interpret(context: User, cmd: P.ChannelCmd): Err[InternalChannelCmd] = {
    implicit def toFail(s: String): Err[InternalChannelCmd] = <~[InternalChannelCmd](s)
    implicit def toSucc(a: InternalChannelCmd): Err[InternalChannelCmd] = ~>[InternalChannelCmd](a)

    cmd match {
      case P.CreateWebChannel(id, lbl, url, section) =>
        context.channels.filter {
          case WebChannel(_, l, u, _, _) => l == lbl || u == url
          case _ => false
        } match {
          case Nil =>
            val validated = ChannelSection.sections.find(_ == section).getOrElse(ChannelSection.General)
            CreateWebChannel(context, id, lbl, url, WebChannel.Image, validated)
          case _ => "Label and URL must be unique."
        }
      case P.RemoveWebChannel(id) => User.webChannels(context).filter(_.id == id) match {
        case h :: x => RemoveWebChannel(context, id)
        case Nil => "Unknown channel."
      }
      case P.CreatePhoneChannel(id, lbl, num, sms, section) =>
       sms match {
         case true => {
           context.channels.filter {
             case SmsChannel(_, l, n, _, _) => l == lbl || n == num
             case _ => false
           } match {
             case Nil =>
               val validated = ChannelSection.sections.find(_ == section).getOrElse(ChannelSection.General)
               CreateSmsChannel(context, id, lbl, num, validated)
           }
         }
         case false => {
           context.channels.filter {
             case PhoneChannel(_, l, n, _, _) => l == lbl || n == num
             case _ => false
           } match {
             case Nil =>
               val validated = ChannelSection.sections.find(_ == section).getOrElse(ChannelSection.General)
               CreatePhoneCallChannel(context, id, lbl, num, validated)
           }
         }
       }
    }
  }

  def process(cmd: InternalChannelCmd): Err[User] = {
    cmd match {
      case CreateWebChannel(u, id, lbl, url, _,  sect) =>
        ~>(u.copy(channels = WebChannel(id, lbl, url, sect) :: u.channels))
      case RemoveWebChannel(u, id) =>
        ~>(u.copy(channels = u.channels.filterNot(_.id == id)))
      case CreateSmsChannel(u, id, lbl, num, section) =>
        ~>(u.copy(channels= SmsChannel(id, lbl, num, section) :: u.channels))
      case CreatePhoneCallChannel(u, id, lbl, num, section) =>
        ~>(u.copy(channels= PhoneChannel(id, lbl, num, section) :: u.channels))
    }
  }

}

object ChannelManager {

  def nextId = ChannelId(UUID.randomUUID.toString)

  sealed trait InternalChannelCmd extends Context[User]
  case class CreateWebChannel(context: User,
                              id: ChannelId,
                              label: String,
                              url: String,
                              imageUrl: String,
                              section: String) extends InternalChannelCmd
  case class RemoveWebChannel(context: User, id: ChannelId) extends InternalChannelCmd

  case class CreateSmsChannel(context: User,
                                 id: ChannelId,
                                 label: String,
                                 number: String,
                                 section: String) extends InternalChannelCmd

  case class CreatePhoneCallChannel(context: User,
                              id: ChannelId,
                              label: String,
                              number: String,
                              section: String) extends InternalChannelCmd
  case class RemovePhoneChannel(context: User, id: ChannelId) extends InternalChannelCmd
}
