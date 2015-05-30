package engine.io.storage

import engine.io.storage.Rest.Path
import play.api.Logger
import play.api.libs.json.{Json, Reads}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RestQueryable[T] {
    val client: Rest
    val location: Path

    def query(ast: QueryStatement)(implicit reads: Reads[T]): Future[Option[T]] = {
       client.get(location ::: List(ast.asString)).map{r =>
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
        l.asString + separator
    }
}


trait Argument extends QueryStatement

case class SimpleArgument[T <: SimpleArgument.SimpleArgumentTypes](v: T) extends Argument {
    def asString = v.toString
}
object SimpleArgument{
    trait SimpleArgumentTypes
    case object Str extends SimpleArgumentTypes
    case object Number extends SimpleArgumentTypes
}

case class StatementArgument(s: Statement) extends Argument {
    def asString = s.asString
}

trait QueryOperator extends QueryStatement
