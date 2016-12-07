package domain.wordcount

import javax.inject._

import scala.collection.mutable
import scala.concurrent.Future

import domain.wordcount.Model._
import domain.{Id, Repository}
import org.joda.time.DateTime

trait WordCountRepository extends Repository[WordCount, WordCountDetails]

@Singleton
class WordCountRepositoryImpl @Inject()(
  store: mutable.Map[Id[WordCount], WordCount]
) extends WordCountRepository {

  def find(id: Id[WordCount]): Future[Option[WordCount]] = Future.successful(store.get(id))

  def findAll(): Future[Seq[WordCount]] = Future.successful(store.values.toSeq)

  def save(data: WordCountDetails): Future[WordCount] = {
    val entity = WordCount(
      WordCountHeader(
        Id.create[WordCount](),
        DateTime.now.wordCountTimestamp
      ),
      data
    )
    store.update(entity.header.id, entity)
    Future.successful(entity)
  }
}
