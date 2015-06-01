package engine.io.storage

import engine.io.storage.Rest.Path
import play.api.Logger
import play.api.libs.json.{Json, Reads}
import play.api.libs.ws.{WSRequestHolder, WSResponse}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RestQueryable[T] {
    def get(p: Path): WSRequestHolder
    val location: Path

    def query(ast: Seq[(String, String)])(implicit reads: Reads[T]): Future[Option[T]] = {
       get(Nil).withQueryString(ast: _*).get().map{r =>
           r.status match {
               case x if x >= 200 && x < 300 =>
                   (Json parse r.body).validate[T].asOpt
               case x if x >=400 =>
                   Logger.error(r.statusText)
                   None
               case _ =>
                   Logger.warn(r.statusText)
                   None
       }}
    }
}

trait QueryStatement {
    def asString: String
}
case class Statement(separator: String)(l: Argument, op: QueryOperator, r: Argument) extends QueryStatement {
    def asString = {
        l.asString + separator + op.asString + separator + r.asString
    }
}

trait Argument extends QueryStatement

case class SimpleArgument[T <: SimpleArgument.SimpleArgumentTypes, A](v: A)(implicit f: A => T) extends Argument {
    def asString = v.toString
}
object SimpleArgument{
    trait SimpleArgumentTypes
    case object Str extends SimpleArgumentTypes
    case object Number extends SimpleArgumentTypes

    implicit def strConv(s: String): SimpleArgumentTypes = Str
    implicit def intConv(i: Int): SimpleArgumentTypes = Number
    implicit def doubleConv(i: Double): SimpleArgumentTypes = Number
}

case class StatementArgument(s: Statement) extends Argument {
    def asString = s.asString
}

trait QueryOperator extends QueryStatement
case class StringOperator(s: String) extends QueryOperator{
    def asString = s
}
