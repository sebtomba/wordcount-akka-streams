package services

import scala.concurrent.Future

trait Service[-Req, +Res] {
  def apply(req: Req): Future[Res]
}

object Service {
  def apply[Req, Res](f: Req => Future[Res]): Service[Req, Res] = {
    new Service[Req, Res] {
      override def apply(request: Req): Future[Res] = {
        f(request)
      }
    }
  }
}
