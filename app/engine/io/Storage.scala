//package engine.io
//
//import engine.domain.Error.Err
//
//import scala.concurrent.Future
//
//
//case class StorageReader[A, B](run: A => Option[B])
//
//trait Producer[O] {
//  def next: Err[O]
//  def asyncNext: Future[Err[O]] = Future(next)
//}
//
//trait Consumer[K,I] {
//  def store(f: I => K)(i: I): Err[I]
//  def storeAsync(f: I => K)(i: I): Future[Err[I]] = Future(store(f)(i))
//}
//
//trait KeyedStore[K, I] {
//  def fetch(k: K): Err[I]
//  def fetchAsync(k: K): Future[Err[I]] = Future(fetch(k))
//}
//
//
//
//
