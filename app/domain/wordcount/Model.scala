package domain.wordcount

import domain.{@@, Id}
import org.joda.time.DateTime

object Model {

  trait Timestamp
  type WordCountTimestamp = DateTime @@ Timestamp

  trait Total
  type WordCountTotal = Long @@ Total

  trait Name
  type WordName = String @@ Name

  trait Density
  type WordDensity = Long @@ Density

  case class Word(
    name: WordName,
    count: WordDensity
  )

  case class WordCountHeader(
    id: Id[WordCount],
    timestamp: WordCountTimestamp
  )

  case class WordCountDetails(
    total: WordCountTotal,
    density: Seq[Word]
  )

  case class WordCount(
    header: WordCountHeader,
    details: WordCountDetails
  )

  implicit class TaggedLong(val l: Long) {
    def wordDensity: WordDensity = l.asInstanceOf[WordDensity]
    def wordCountTotal: WordCountTotal = l.asInstanceOf[WordCountTotal]
  }

  implicit class TaggedString(val s: String) {
    def wordName: WordName = s.asInstanceOf[WordName]
  }

  implicit class TaggedDateTime(val d: DateTime) {
    def wordCountTimestamp: WordCountTimestamp = d.asInstanceOf[WordCountTimestamp]
  }

}
