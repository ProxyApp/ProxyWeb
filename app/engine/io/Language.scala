package engine.io

import proxy.State


trait IoCmd
case class Filter[A](p: A => Boolean) extends IoCmd
case class Search[A](args: Filter[A]) extends IoCmd
case class Store[A](data: A) extends IoCmd


trait Source[A] {
  def get(q: Search[A]): Option[A]
}

trait Sink[A] {
  def put(s: Store[A]): Option[Sink[A]] //just for the error message
}

//trait Storage[A] extends Source[A] with Sink[A]{
//  val source: Source[A]
//  val sink: Sink[A]
//
//  def interpret()
//}