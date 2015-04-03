package engine.util


trait Functor[F[_]] {
  def map[A, B](a: F[A])(f: A => B): F[B]
}

trait Monad[M[_]] extends Functor[M]{
  def zero[A](a: => A): M[A]
  def flatMap[A,B](a: M[A])(f: A => M[B]): M[B]

  def map[A, B](m: M[A])(f: A => B) =
    flatMap(m)(a => zero(f(a)))
}


