package engine.domain

import scala.util.Either.RightProjection

object Error {

  type Err[A] = RightProjection[String, A]

  case class ~>[A](v: A)
  case class <~[A](e: String)

  implicit def rAsRP[A](v: ~>[A]): Err[A] =
    Right(v.v).right

  implicit def lAsRP[A](e: <~[A]): Err[A] =
    Left(e.e).right

}
