package controllers

import scala.concurrent.Future

import akka.util.ByteString
import domain.Id
import domain.wordcount.Model.WordCount
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, JsString, Json}
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import services.WordCountService._
import services.{Service, WordCountService}
import utils.WordCountDataProvider

class MainControllerSpecs
  extends PlaySpec
    with OneAppPerSuite
    with Results
    with MockitoSugar
    with WordCountDataProvider {

  val executionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit override lazy val app = new GuiceApplicationBuilder().configure(Map("wordcount.frameLength" -> 10)).build()

  "MainController" when {
    "requesting index" should {
      "respond with OK" in {
        val wordCountService = mock[WordCountService]
        val controller = new MainController(wordCountService, app.configuration, executionContext)
        val result = await(controller.index().apply(FakeRequest()))

        result.header.status must equal(OK)
      }
    }

    "requesting wordcount for 'abc def ghi'" should {
      "count the words and save them" in {
        implicit val materializer = app.materializer
        val wordCountService = mock[WordCountService]
        val createWordCount = mock[Service[CreateWordCountRequest, CreateWordCountResponse]]
        when(wordCountService.createWordCount) thenReturn createWordCount
        when(createWordCount.apply(any[CreateWordCountRequest])) thenReturn Future.successful(CreateWordCountResponse(wordCount1))
        val controller = new MainController(wordCountService, app.configuration, executionContext)
        val request = FakeRequest(POST, "/wordcount").withTextBody("abc def ghi")
        val result = await(call(controller.countWords, request))
        val body = Json.parse(await(result.body.consumeData).decodeString(ByteString.UTF_8))

        result.header.status must equal(OK)
        verify(createWordCount).apply(CreateWordCountRequest(Map("abc" -> 1, "def" -> 1, "ghi" -> 1)))
        (body \ "id").get must equal(JsString(wordCount1.header.id.value))
      }
    }

    "requesting wordcount for 'abcdefghijkl mno'" should {
      "fail with a frame error" in {
        implicit val materializer = app.materializer
        val wordCountService = mock[WordCountService]
        val controller = new MainController(wordCountService, app.configuration, executionContext)
        val request = FakeRequest(POST, "/wordcount").withTextBody("abcdefghijkl mno")
        val result = await(call(controller.countWords, request))
        val body = await(result.body.consumeData).decodeString(ByteString.UTF_8)

        result.header.status must equal(BAD_REQUEST)
        body must equal("Read 16 bytes which is more than 10 without seeing a line terminator")
      }
    }

    "requesting wordcount for JSON" should {
      "fail with a unsupported media type error" in {
        implicit val materializer = app.materializer
        val wordCountService = mock[WordCountService]
        val controller = new MainController(wordCountService, app.configuration, executionContext)
        val request = FakeRequest(POST, "/wordcount").withJsonBody(JsObject(Seq("test" -> JsString("abc"))))
        val result = await(call(controller.countWords, request))
        val body = await(result.body.consumeData).decodeString(ByteString.UTF_8)

        result.header.status must equal(UNSUPPORTED_MEDIA_TYPE)
        body must equal("Expecting text/plain or application/octet-stream")
      }
    }

    "requesting all previous word counts" should {
      "respond with all saved word counts" in {
        implicit val materializer = app.materializer
        val wordCountService = mock[WordCountService]
        val findWordCounts = mock[Service[FindWordCountsRequest, FindWordCountsResponse]]
        when(wordCountService.findWordCounts) thenReturn findWordCounts
        when(findWordCounts.apply(any[FindWordCountsRequest])) thenReturn Future.successful(FindWordCountsResponse(wordCountsAll.map(_.header)))
        val controller = new MainController(wordCountService, app.configuration, executionContext)
        val request = FakeRequest(GET, "/wordcount")
        val result = await(call(controller.getWordCounts, request))
        val body = await(result.body.consumeData).decodeString(ByteString.UTF_8)

        result.header.status must equal(OK)
        verify(findWordCounts).apply(FindWordCountsRequest())
      }
    }

    "requesting a previous word count" should {
      "respond with the requested word count" in {
        implicit val materializer = app.materializer
        val wordCountService = mock[WordCountService]
        val findWordCount = mock[Service[FindWordCountRequest, FindWordCountResponse]]
        when(wordCountService.findWordCount) thenReturn findWordCount
        when(findWordCount.apply(any[FindWordCountRequest])) thenReturn Future.successful(FindWordCountResponse(Some(wordCount1)))
        val controller = new MainController(wordCountService, app.configuration, executionContext)
        val request = FakeRequest(GET, "/wordcount")
        val result = await(call(controller.getWordCount("test"), request))
        val body = Json.parse(await(result.body.consumeData).decodeString(ByteString.UTF_8))

        result.header.status must equal(OK)
        verify(findWordCount).apply(FindWordCountRequest(Id[WordCount]("test")))
        (body \ "id").get must equal(JsString(wordCount1.header.id.value))
      }
    }

  }

}
