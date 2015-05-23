package proxy.controllers

import engine.domain._
import engine.io.storage.{FirebaseUserStorage, RestStorage}
import engine.service.Protocol._
import engine.service.{ContactManager, GroupManager, ChannelManager, UserService}
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.{Writes, Json}
import play.api.mvc._
import proxy.controllers.json._

trait UsersContext extends Controller {
  type R = Action[AnyContent]
  val svc: UserService

  /**
   * Returns all Proxy Users
   * @return
   */
  def all = Action {
    Ok("Not Implemented")
  }

  /**
   * Returns the full User construct for the given id.
   * @param id
   * @return
   */
  def user(id: String) = Action {
    svc.user(UserId(id))
      .fold(NotFound(id))(r => Ok(r))
  }

  /**
   * Returns all channels for a given user, provided that user exists
   * @param id
   * @return
   */
  def channels(id: String) = Action {
    svc.user(UserId(id))
      .fold(NotFound(id))(r => Ok(r.channels))
  }

  /**
   * Returns all of the given user's contacts
   * @param id
   * @return
   */
  def contacts(id: String) = Action {
    svc.user(UserId(id))
      .fold(NotFound(id))(r => Ok(r.contacts))
  }

  /** Returns all of the groups for a given user
    *
    * @param id
    * @return
    */
  def groups(id: String) = Action {
    svc.user(UserId(id))
      .fold(NotFound(id))(r => Ok(r.groups))
  }


  def group(id: String, gid: String) = Action {
    svc.user(UserId(id))
      .fold(NotFound(id))(r =>
      r.groups.find(g => g.id == GroupId(gid))
        .fold(NotFound((id)))(r => Ok(r))
      )
  }

  def groupContacts(id: String, gid: String) = Action {
    svc.user(UserId(id))
      .fold(NotFound(id))(r =>
      r.groups.find(g => g.id == GroupId(gid))
        .fold(NotFound((id)))(r => Ok(r.contacts))
      )
  }

  def groupChannels(id: String, gid: String) = Action {
  svc.user(UserId(id))
    .fold(NotFound(id))(r =>
    r.groups.find(g => g.id == GroupId(gid))
      .fold(NotFound((id)))(r => Ok(r.contacts))
    )
  }

  def newChannel(id: String) = Action(parse.json[WireCreateWebChannel]) { request =>
      val wire = request.body
      val protocol = CreateWebChannel(ChannelManager.nextId, wire.label, wire.url)
      svc.user(UserId(id))
        .fold(NotFound(id))(u =>
        (for{
          c <- svc.channelManager.interpret(u, protocol)
          b <- svc.channelManager.process(c)
        } yield b)
        .fold(e => BadRequest(e), r => {
          svc.store.writeContext(r)
          Ok(r)
        })
      )
    }

  def newGroup(id: String) = Action(parse.json[WireCreateGroup]) { req =>
    val w = req.body
    val p = CreateGroup(w.label, GroupManager.nextId)
    svc.user(UserId(id))
      .fold(NotFound(id))(u =>
      (for {
        c <- svc.groupManager.interpret(u, p)
        b <- svc.groupManager.process(c)
      } yield b)
      .fold(e => BadRequest(e), r =>{
        svc.store.writeContext(r)
        Ok(r)
      })
    )
  }

  //todo ccoffey actually implement this
  //private def processServiceCmd[A, C](uid: UserId, c: C, svc: )

  def newGroupContact(id: String, gid: String) = Action(parse.json[WireAddGroupContact]) { req =>
    val w = req.body
    val p = AddContactToGroup(GroupId(gid), UserId(w.contact_id))
    svc.user(UserId(id))
      .fold(NotFound(id))(u =>
      (for {
        a <- svc.groupManager.interpret(u, p)
        b <- svc.groupManager.process(a)
      } yield b)
      .fold(e => BadRequest(e), r => {
        svc.store.writeContext(r)
        Ok(r)
      })
    )
  }

  def newGroupChannel(id: String, gid: String) = Action(parse.json[WireAddGroupChannel]) {req =>
    val p = AddChannelToGroup(GroupId(gid), ChannelId(req.body.channel_id))
    svc.user(UserId(id))
      .fold(NotFound(id))(u =>
      (for {
        a <- svc.groupManager.interpret(u, p)
        b <- svc.groupManager.process(a)
      }yield b)
      .fold(e => BadRequest(e),r  => {
        svc.store.writeContext(r)
        Ok(r)
      })
    )
  }

  def removeChannel(id: String, cid: String) = Action {req =>
    val p = RemoveWebChannel(ChannelId(cid))
    svc.user(UserId(id))
      .fold(NotFound(id))(u =>
      (for {
        a <- svc.channelManager.interpret(u, p)
        b <- svc.channelManager.process(a)
      }yield b)
      .fold(e => BadRequest(e), r => {
        svc.store.writeContext(r)
        Ok(r)
      })
    )
  }

  def removeGroup(id: String, gid: String) = Action { req =>
    val p = RemoveGroup(GroupId(gid))
    svc.user(UserId(id))
      .fold(NotFound(id))(u =>
      (for {
        a <- svc.groupManager.interpret(u, p)
        b <- svc.groupManager.process(a)
      } yield b)
      .fold(e => BadRequest(e), r => {
        svc.store.writeContext(r)
        Ok(r)
      })
    )
  }

  def removeGroupContact(id: String, gid: String, cid: String) = Action {req =>
    val p = RemoveContactFromGroup(GroupId(gid), UserId(cid))
    svc.user(UserId(id))
      .fold(NotFound(id))(u =>
      (for {
        a <- svc.groupManager.interpret(u, p)
        b <- svc.groupManager.process(a)
      } yield b)
      .fold(e => BadRequest(e), r => {
        svc.store.writeContext(r)
        Ok(r)
      }))
  }

  def removeGroupChannel(id: String, gid: String, cid: String) = Action {req =>
      val p = RemoveChannelFromGroup(GroupId(gid), ChannelId(cid))
    svc.user(UserId(id))
      .fold(NotFound(id))(u =>
      (for {
        a <- svc.groupManager.interpret(u, p)
        b <- svc.groupManager.process(a)
      } yield b)
      .fold(e => BadRequest(e), r => {
        svc.store.writeContext(r)
        Ok(r)
      }))
  }

  def replaceContext(id: String) = Action {
    NotImplemented
  }

}

object Users extends UsersContext{

  val svc = new UserService {

    override val channelManager: ChannelManager = new ChannelManager {}
    override val contactManager: ContactManager = new ContactManager {
      //note this is a bs implementation. I need to filter out channels taht I do not have access to
      def contact(id: UserId) = user(id).map(u => Contact(u.id, u.first + " " + u.last, u.channels))
    }
    override val groupManager: GroupManager = new GroupManager {}
    override var store: RestStorage[User] = FirebaseUserStorage
  }

}

case class BatchUpdateChannel(m: Map[String, Boolean])


