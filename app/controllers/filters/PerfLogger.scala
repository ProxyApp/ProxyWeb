package controllers.filters

import play.api.Logger
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PerfLogger extends Filter {

  private val Timing_Header = "Request-Duration"

  def apply(next: RequestHeader => Future[Result])(h: RequestHeader) = {
    val s = System.currentTimeMillis
    next(h).map { res =>
      val end = System.currentTimeMillis
      val duration = end - s

      Logger.info(s"${h.method} ${h.uri} took ${duration}ms before returning ${res.header.status}")
      res.withHeaders(Timing_Header -> duration.toString)
    }
  }
}
