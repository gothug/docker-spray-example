package mailer

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.routing.RoundRobinRouter
import akka.util.Timeout
import com.softwaremill.example.JsonSupport._
import com.typesafe.scalalogging.slf4j.Logger
import moviesearch.{KickassQuery, MovieQueryResult}
import org.slf4j.LoggerFactory
import spray.client.pipelining._
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

    val url = "http://localhost:8080/search/kickass"

    val processItemActor = actorSystem.actorOf(Props(new ProcessItemActor()).withRouter(RoundRobinRouter(8)), "processitem")

    val moviesList = movieTitles.list //.filter(x => ("london".r findFirstIn x.title).isDefined)

    val responses: List[Future[MovieQueryResult]] =
      for {
        m <- moviesList
      } yield (processItemActor ? Item(url, m))(500 seconds).mapTo[MovieQueryResult]

    val results: List[MovieQueryResult] = Await.result(Future.sequence(responses), 6500 seconds)

    logger.info("Finished processing..")

    def toHtml(title: String, year: Int, link: String) = {
      s"""<p>Title: $title, Year: $year<br><a href="$link">$link</a></p>""".stripMargin
    }

    val htmls =
      for {
        (WatchListParsedMovie(title, year), link) <- moviesList zip results.map(_.link)
      } yield toHtml(title, year, link)

    sendMail(htmls.mkString("<br>"))
  }

  def sendMail(html: String) = {
    val proto = "https://"
    val domain = "mandrillapp.com"
    val basepath = "/api/1.0"
    val path = "/messages/send.json"

    val url = proto + domain + basepath + path

    val htmlEscaped = html.replace(""""""", """\"""")

    val emailJson =
      s"""|{
          |   "key" : "BAXsOmtwxAELZKVQWohlzQ",
          |   "message" : {
          |     "html" : "$htmlEscaped",
          |     "text" : "http://google.com",
          |     "subject" : "example subject",
          |     "from_email" : "kojuhovskiy@gmail.com",
          |     "from_name" : "Example Name",
          |     "to" : [
          |       {
          |         "email" : "kojuhovskiy@gmail.com",
          |         "name" : "Vasek",
          |         "type" : "to"
          |       }
          |     ],
          |     "headers" : {
          |       "Reply-To" : "kojuhovskiy@gmail.com"
          |     },
          |     "auto_html": true
          |   }
          |}""".stripMargin

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val response: Future[HttpResponse] = pipeline(Post(url, emailJson))

    logger.info("Sending e-mail..")

    val r = Await.result(response, 65 seconds)

    if (r.status.toString == "200 OK") {
      logger.info("Email sent successfully")
    } else {
      logger.info("Problems with sending email")
      logger.info(r.entity.toString)
    }

    val d = 1
  }
}

case class Item(url: String, query: WatchListParsedMovie)

class ProcessItemActor() extends Actor {
  import context.dispatcher

  override def receive = {
    case Item(url, wlMovie) => sender ! processRequest(url, wlMovie)
  }

  def processRequest(url: String, movie: WatchListParsedMovie): MovieQueryResult = {
    val pipeline: HttpRequest => Future[MovieQueryResult] = sendReceive ~> unmarshal[MovieQueryResult]
    val response: Future[MovieQueryResult] = pipeline(Post(url, KickassQuery(movie.title, None, movie.year)))
    Await.result(response, 30 seconds)
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
