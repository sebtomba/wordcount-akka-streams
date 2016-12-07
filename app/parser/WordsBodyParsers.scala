package parser

import scalaz.Scalaz._

import akka.stream.scaladsl.{Flow, Framing, Keep, Sink}
import akka.util.ByteString
import play.api.Configuration
import play.api.http.LazyHttpErrorHandler
import play.api.http.Status._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.streams.Accumulator
import play.api.mvc.{BodyParser, BodyParsers, RequestHeader}

trait WordsBodyParsers {

  import WordsBodyParsers._

  val configuration: Configuration

  private lazy val FrameDelimiter = ByteString(" ")
  private lazy val AcceptedContentTypesStr = WordsAcceptedContentTypes.mkString(" or ")
  val DefaultFrameLength: Int = configuration.getInt("wordcount.frameLength").getOrElse(1000)
  val DefaultWordGroupSize: Int = configuration.getInt("wordcount.groupSize").getOrElse(1000)

  object parseWords {

    def wordcount: BodyParser[Map[String, Long]] = wordcount(DefaultFrameLength, DefaultWordGroupSize)

    def wordcount(frameLength: Int, wordGroupSize: Int): BodyParser[Map[String, Long]] = BodyParsers.parse.when(
      req => checkContentType(req.contentType, tolerant = true),
      bodyParser(frameLength, wordGroupSize),
      createBadResult(s"Expecting $AcceptedContentTypesStr", UNSUPPORTED_MEDIA_TYPE)
    )

    private def bodyParser(frameLength: Int, wordGroupSize: Int) = BodyParser { req =>
      val sink = Flow[ByteString]
        // Split frames by the space character, allow a MaximumFrameLength bytes per word
        .via(Framing.delimiter(FrameDelimiter, frameLength, allowTruncation = true))
        // Turn each frame to a String and split it by words regex
        .mapConcat(b => split(decodeString(b, req.charset)))
        // Collect blocks of words
        .grouped(wordGroupSize)
        // Count the words in a block
        .map(countWords)
        // Merge the Maps with word counts
        .toMat(Sink.fold(Map.empty[String, Long])(_ |+| _))(Keep.right)

      Accumulator(sink).map(Right.apply).recoverWith {
        case e => createBadResult(e.getMessage)(req).map(Left.apply)
      }
    }
  }

}

object WordsBodyParsers {
  private lazy val WordsMatcher = "\\W+".r
  private lazy val WordsAcceptedContentTypes = Seq("text/plain", "application/octet-stream")

  private def split(toSplit: String) =
    WordsMatcher.split(toSplit.toLowerCase)
      .filterNot(_.isEmpty)
      .toList

  private def decodeString(bytes: ByteString, charset: Option[String]) =
    bytes.decodeString(charset.getOrElse(ByteString.UTF_8))

  private def checkContentType(contentType: Option[String], tolerant: Boolean = false) =
    contentType match {
      case Some(ct) if WordsAcceptedContentTypes.contains(ct.toLowerCase) => true
      case None => tolerant // tolerant if there is no Content-Type header
      case _ => false
    }

  private def countWords(words: Seq[String]) = words.groupBy(identity).mapValues(_.length.toLong)

  private def createBadResult(msg: String, statusCode: Int = BAD_REQUEST) = { request: RequestHeader =>
    LazyHttpErrorHandler.onClientError(request, statusCode, msg)
  }

}

