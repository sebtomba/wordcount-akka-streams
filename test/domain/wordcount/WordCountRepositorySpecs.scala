package domain.wordcount

import scala.collection.mutable

import domain.Id
import domain.wordcount.Model._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import utils.WordCountDataProvider

class WordCountRepositorySpecs
  extends WordSpec
    with Matchers
    with ScalaFutures
    with WordCountDataProvider {

  def createStore() = mutable.Map.empty[Id[WordCount], WordCount]

  def createRepositoryWith(wordCounts: Seq[WordCount]) = {
    val store = createStore()
    store.add(wordCounts)
    new WordCountRepositoryImpl(store)
  }

  implicit class AddWordCountToStore(val store: mutable.Map[Id[WordCount], WordCount]) {
    def add(wordCount: WordCount): Unit = store.update(wordCount.header.id, wordCount)

    def add(wordCounts: Seq[WordCount]): Unit = wordCounts.foreach(add(_))
  }

  "WordCountRepository" when {

    "adding a new entity" should {
      "save and return the new WordCount" in {
        val store = createStore()
        val repo = new WordCountRepositoryImpl(store)
        val result = repo.save(wordCount1.details)

        whenReady(result) { r =>
          store.nonEmpty should equal(true)
          r.details should equal(wordCount1.details)
          store.values.head.details should equal(wordCount1.details)
        }
      }
    }

    "searching for an saved entity" should {
      "return the searched WordCount" in {
        val repo = createRepositoryWith(wordCountsAll)
        val result = repo.find(wordCount1.header.id)

        result.futureValue should equal(Some(wordCount1))
      }
    }

    "searching for an unsaved entity" should {
      "return None" in {
        val repo = createRepositoryWith(wordCountsAll)
        val result = repo.find(Id[WordCount]("fake"))

        result.futureValue should equal(None)
      }
    }

    "searching for all entities" should {
      "return all saved WordCounts" in {
        val repo = createRepositoryWith(wordCountsAll)
        val result = repo.findAll()

        result.futureValue.toSet should equal(wordCountsAll.toSet)
      }
    }

  }

}
