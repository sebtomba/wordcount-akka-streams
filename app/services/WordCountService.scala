package services

import javax.inject._

import scala.concurrent.ExecutionContext

import domain.Id
import domain.wordcount.Model._
import domain.wordcount.WordCountRepository
import services.WordCountService._

object WordCountService {

  import domain.wordcount.Model._

  case class CreateWordCountRequest(wordCounts: Map[String, Long])
  case class CreateWordCountResponse(wordCount: WordCount)

  case class FindWordCountRequest(id: Id[WordCount])
  case class FindWordCountResponse(wordCount: Option[WordCount])

  case class FindWordCountsRequest()
  case class FindWordCountsResponse(wordCounts: Seq[WordCountHeader])

}

trait WordCountService {
  def createWordCount: Service[CreateWordCountRequest, CreateWordCountResponse]

  def findWordCount: Service[FindWordCountRequest, FindWordCountResponse]

  def findWordCounts: Service[FindWordCountsRequest, FindWordCountsResponse]
}

@Singleton
class WordCountServiceImpl @Inject()(
  wordCountRepository: WordCountRepository,
  implicit val executionContext: ExecutionContext
) extends WordCountService {

  override def createWordCount: Service[CreateWordCountRequest, CreateWordCountResponse] =
    Service { request =>
      val words = request.wordCounts.map { case (word, count) => Word(word.wordName, count.wordDensity) }
      val details = WordCountDetails(request.wordCounts.values.sum.wordCountTotal, words.toSeq)
      wordCountRepository.save(details).map(CreateWordCountResponse)
    }

  override def findWordCount: Service[FindWordCountRequest, FindWordCountResponse] =
    Service { request =>
      wordCountRepository.find(request.id).map(FindWordCountResponse)
    }

  override def findWordCounts: Service[FindWordCountsRequest, FindWordCountsResponse] =
    Service { request =>
      wordCountRepository.findAll()
        .map(_.map(_.header))
        .map(FindWordCountsResponse)
    }

}
