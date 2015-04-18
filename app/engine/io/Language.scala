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

case class Query[A](cond: Condition[A])
case class Condition[A](left: A, operand: OP[A], right: A)

//open question is whether to keep this as a query language with generic types,
// which suggests everything descends from some common type explicitly,
// or update this to use a common "Persisted" typeclass, which requires
// implementers to provide the mapping from the type to basic types


trait OP[A] extends IoCmd
case class Equals[A](l: A, r: A) extends OP[A]
case class LessThan[A](l: A, r: A) extends OP[A]
case class LessThanEquals[A](l: A, r: A) extends OP[A]
case class Or[A](l: A, r: A) extends OP[A]




trait Source[A] {
  def get(q: Search[A]): Option[A]
}

trait Sink[A] {
  def put(s: Store[A]): Option[Sink[A]] //just for the error message
}

