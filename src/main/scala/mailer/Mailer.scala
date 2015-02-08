package mailer

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.slf4j.Logger
import com.softwaremill.example.JsonSupport._
import moviesearch.{KickassQuery, MovieQueryResult}
import org.slf4j.LoggerFactory
import spray.can.Http
import spray.client.pipelining._
import spray.http.ContentTypes._
import spray.http.HttpMethods._
import spray.http._
import spray.httpx.SprayJsonSupport._
import watchlistparser.{WatchListMovies, _}

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by kojuhovskiy on 05/02/15.
 */
//class Mailer(implicit val actorSystem: ActorSystem, implicit val timeout: Timeout, implicit val logger: Logger) {
class Mailer(implicit val actorSystem: ActorSystem, implicit val timeout: Timeout) {
  import actorSystem.dispatcher

  val logger = Logger(LoggerFactory.getLogger("mailer"))

  def processWatchLists() = {
    def getMovieTitles: WatchListMovies = {
      val pipeline: HttpRequest => Future[WatchListMovies] = sendReceive ~> unmarshal[WatchListMovies]
      val url = "http://localhost:8080/watchlist/imdb"
      val watchlistLink =
        "http://www.quickproxy.co.uk/index.php?q=aHR0cDovL3d3dy5pbWRiLmNvbS91c2VyL3VyOTExMjg3OC93YXRjaGxpc3Q%2FcmVmXz13dF9udl93bF9hbGxfMA%3D%3D&hl=2ed"
      val response = pipeline(Post(url, WatchListQuery(watchlistLink)))
      Await.result(response, 25 seconds)
    }

    logger.info("Mailer..")

    val movieTitles = getMovieTitles

    val pipeline: HttpRequest => Future[MovieQueryResult] = sendReceive ~> unmarshal[MovieQueryResult]
    val url = "http://localhost:8080/search/kickass"

    val responses: List[Future[MovieQueryResult]] =
      for {
        m <- movieTitles.list
      } yield pipeline(Post(url, KickassQuery(m.title, None, m.year)))

    val results: List[MovieQueryResult] = Await.result(Future.sequence(responses), 65 seconds)

//    sendMail(r.html)

    val d = 1
  }

  def sendMail(html: String) = {
    val proto = "https://"
    val domain = "mandrillapp.com"
    val basepath = "/api/1.0"
    val path = "/messages/send.json"

    val url = proto + domain + basepath + path

    val emailJson =
      s"""{
          "key" : "BAXsOmtwxAELZKVQWohlzQ",
          "message" : {
            "html" : "$html",
            "text" : "Example text content",
            "subject" : "example subject",
            "from_email" : "kojuhovskiy@gmail.com",
            "from_name" : "Example Name",
            "to" : [
              {
                "email" : "kojuhovskiy@gmail.com",
                "name" : "Vasek",
                "type" : "to"
              }
            ],
            "headers" : {
              "Reply-To" : "kojuhovskiy@gmail.com"
            }
          }
        }"""

    val response: Future[HttpResponse] =
      (IO(Http) ?
        HttpRequest(method = POST, uri = Uri(url),
          entity = HttpEntity(`application/json`, emailJson))).mapTo[HttpResponse]

    logger.info("Sending e-mail..")
  }
}

object Mailer {
  def main(args: Array[String]) = {
    implicit val actorSystem = ActorSystem()
    implicit val timeout = Timeout(30.second)

    val mailer = new Mailer()
    mailer.processWatchLists()

    actorSystem.shutdown()
  }
}
