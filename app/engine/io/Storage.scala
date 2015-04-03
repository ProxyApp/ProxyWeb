package engine.io


case class StorageReader[A, B](run: A => Option[B])
