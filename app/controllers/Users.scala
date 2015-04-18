package proxy.controllers

import play.api.mvc._

object Users extends Controller {

  def all = ????
  def user(id: String) = ????
  def channels(id: String) = ????
  def channel(id: String, cid: String) = ????
  def contacts(id: String) = ????
  def groups(id: String) = ????
  def group(id: String, gid: String) = ????
  def groupContacts(id: String, gid: String) = ????
  def groupChannels(id: String, gid: String) = ????

  def newChannel(id: String) = ????
  def newGroup(id: String) = ????
  def newGroupContact(id: String, gid: String) = ????
  def newGroupChannel(id: String, gid: String) = ????

  def removeChannel(id: String, cid: String) = ????
  def removeGroup(id: String, gid: String) = ????
  def removeGroupContact(id: String, gid: String, cid: String) = ????
  def removeGroupChannel(id: String, gid: String, cid: String) = ????

  def replaceContext(id: String) = ????

  private def ???? = Action {
    Forbidden
  }
}
