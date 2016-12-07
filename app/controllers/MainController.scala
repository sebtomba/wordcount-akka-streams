package controllers

import javax.inject._

import scala.concurrent.ExecutionContext

import domain.Id
import domain.wordcount.Model._
import parser.WordsBodyParsers
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller}
import services.WordCountService
import services.WordCountService._

@Singleton
class MainController @Inject()(
  wordCountService: WordCountService,
  val configuration: Configuration,
  implicit val executionContext: ExecutionContext
) extends Controller with WordsBodyParsers {

  import json.WordCountJson._

  def index = Action {
    Ok("")
  }

  def countWords: Action[Map[String, Long]] = Action.async(parseWords.wordcount) { request =>
    wordCountService
      .createWordCount(CreateWordCountRequest(request.body))
      .map(res => Ok(Json.toJson(res.wordCount)))
  }

  def getWordCounts: Action[AnyContent] = Action.async { request =>
    wordCountService
      .findWordCounts(FindWordCountsRequest())
      .map(res => Ok(Json.toJson(res.wordCounts)))
  }

  def getWordCount(id: String): Action[AnyContent] = Action.async { request =>
    wordCountService
      .findWordCount(FindWordCountRequest(Id[WordCount](id)))
      .map(res =>
        if (res.wordCount.isDefined)
          Ok(Json.toJson(res.wordCount))
        else NotFound
      )
  }
}
