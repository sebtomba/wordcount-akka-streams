package domain

import scala.concurrent.Future

trait Repository[Entity, Data] {
  def find(id: Id[Entity]): Future[Option[Entity]]

  def findAll(): Future[Seq[Entity]]

  def save(data: Data): Future[Entity]
}
