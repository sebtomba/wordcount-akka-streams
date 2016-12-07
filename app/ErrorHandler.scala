import scala.concurrent._

import play.api.Logger
import play.api.http.HttpErrorHandler
import play.api.mvc.RequestHeader
import play.api.mvc.Results.{InternalServerError, Status}

class ErrorHandler extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Logger.error(s"${request.method} ${request.uri} returned $statusCode - $message")
    Future.successful(
      Status(statusCode)(message)
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable) = {
    Future.successful(
      InternalServerError(exception.getMessage)
    )
  }
}
