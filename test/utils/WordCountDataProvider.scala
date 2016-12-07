package utils

import domain.Id
import domain.wordcount.Model._
import org.joda.time.DateTime

trait WordCountDataProvider {
  def createWordCount() =
    WordCount(
      WordCountHeader(Id.create[WordCount](), DateTime.now.wordCountTimestamp),
      WordCountDetails(1L.wordCountTotal, Seq(Word("test".wordName, 1L.wordDensity)))
    )

  val wordCount1 = createWordCount()
  val wordCount2 = createWordCount()
  val wordCount3 = createWordCount()
  val wordCountsAll = Seq(wordCount1, wordCount2, wordCount3)

}
