package engine.service

import engine.domain.Error.Err
import engine.domain.User

trait Context[T] {
  val context: T
}

trait ServiceCore[C <: Context[_], P] {

  /**
   * This represents the validation around what may be done to a given User
   * @param in
   * @return
   */
  def interpret(context: User, in: P): Err[C]

  /**
   * This performs the actual modifications of the User context
   * @param in
   * @return
   */
  def process(in: C): Err[User]

}
