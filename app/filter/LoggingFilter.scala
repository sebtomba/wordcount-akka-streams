package filter

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import akka.util.ByteString
import play.api.Logger
import play.api.libs.streams.Accumulator
import play.api.mvc._

class LoggingFilter @Inject()(implicit ec: ExecutionContext) extends EssentialFilter {
  def apply(nextFilter: EssentialAction) = new EssentialAction {
    def apply(requestHeader: RequestHeader): Accumulator[ByteString, Result] = {

      val startTime = System.currentTimeMillis

      val accumulator: Accumulator[ByteString, Result] = nextFilter(requestHeader)

      accumulator.map { result =>

        val endTime = System.currentTimeMillis
        val requestTime = endTime - startTime

        Logger.info(s"${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}")
        result.withHeaders("Request-Time" -> requestTime.toString)

      }
    }
  }
}
