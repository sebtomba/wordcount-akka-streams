package controllers.json

import domain.Id
import domain.wordcount.Model._
import play.api.libs.json.Json._
import play.api.libs.json._

object WordCountJson {

  implicit val wordCountTimestampWrites = new Writes[WordCountTimestamp] {
    def writes(ts: WordCountTimestamp) =
      JsString(ts.toString)
  }

  implicit val wordCountIdWrites: Writes[Id[WordCount]] = idWrites[WordCount]
  implicit val wordCountHeaderWrites: Writes[WordCountHeader] = writes[WordCountHeader]

  implicit val wordCountWrites = new Writes[WordCount] {
    def writes(wc: WordCount) =
      JsObject(Seq(
        "id" -> wordCountIdWrites.writes(wc.header.id),
        "timestamp" -> wordCountTimestampWrites.writes(wc.header.timestamp),
        "total" -> JsNumber(wc.details.total),
        "density" -> JsObject(wc.details.density.map(w => w.name -> JsNumber(w.count)))
      ))
  }

}
