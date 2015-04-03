package engine.io


/*
Could go so far as to add another level of abstraction betweeen these free monads & the
underlying interface that serves as an interpreter. This interpreter may have an arbitrary
set of functions wired into it so the logic for handling requests is always the same, but
any underlying implementation is different (interface).
 */

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

