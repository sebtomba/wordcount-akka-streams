package services

import scala.concurrent.Future

import domain.Id
import domain.wordcount.Model._
import domain.wordcount.WordCountRepository
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import services.WordCountService.{CreateWordCountRequest, FindWordCountRequest, FindWordCountsRequest}
import utils.WordCountDataProvider

class WordCountServiceSpecs
  extends WordSpec
    with Matchers
    with MockitoSugar
    with ScalaFutures
    with WordCountDataProvider {

  val executionContext = scala.concurrent.ExecutionContext.Implicits.global


  "WordCountService" when {

    "adding a new entity" should {
      "save and return the new WordCount" in {

        val repository = mock[WordCountRepository]
        when(repository.save(any[WordCountDetails])) thenReturn Future.successful(wordCount1)
        val wordCountService = new WordCountServiceImpl(repository, executionContext)
        val result = wordCountService.createWordCount(CreateWordCountRequest(Map("test" -> 1)))

        whenReady(result) { r =>
          r.wordCount should equal(wordCount1)
          verify(repository).save(wordCount1.details)
        }
      }
    }

    "searching for an saved entity" should {
      "return the searched WordCount" in {

        val repository = mock[WordCountRepository]
        when(repository.find(wordCount1.header.id)) thenReturn Future.successful(Some(wordCount1))
        val wordCountService = new WordCountServiceImpl(repository, executionContext)
        val result = wordCountService.findWordCount(FindWordCountRequest(wordCount1.header.id))

        whenReady(result) { r =>
          r.wordCount should equal(Some(wordCount1))
          verify(repository).find(wordCount1.header.id)
        }
      }
    }

    "searching for an unsaved entity" should {
      "return None" in {

        val id = Id[WordCount]("fake")
        val repository = mock[WordCountRepository]
        when(repository.find(id)) thenReturn Future.successful(None)
        val wordCountService = new WordCountServiceImpl(repository, executionContext)
        val result = wordCountService.findWordCount(FindWordCountRequest(id))

        whenReady(result) { r =>
          r.wordCount should equal(None)
          verify(repository).find(id)
        }
      }
    }

    "searching for all entities" should {
      "return all saved WordCounts" in {

        val repository = mock[WordCountRepository]
        when(repository.findAll()) thenReturn Future.successful(wordCountsAll)
        val wordCountService = new WordCountServiceImpl(repository, executionContext)
        val result = wordCountService.findWordCounts(FindWordCountsRequest())

        whenReady(result) { r =>
          r.wordCounts should equal(wordCountsAll.map(_.header))
          verify(repository).findAll()
        }
      }
    }

  }

}
