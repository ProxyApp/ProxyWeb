package engine.io.memory

import engine.io.{Search, Store, Sink, Source}

case class SimpleStorage[A](c: Seq[A]) extends Source[A] with Sink[A]{

  def get(q: Search[A]): Option[A] = {
    c.find(q.args.p)
  }

  def put(s: Store[A]): Option[SimpleStorage[A]] = {
    val ns = c :+ s.data
    Some(SimpleStorage(ns))
  }
}


