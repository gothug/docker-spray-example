package com.softwaremill.example

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.routing.RoundRobinRouter
import akka.util.Timeout
import com.typesafe.scalalogging.slf4j.Logger
import moviesearch._
import org.slf4j.LoggerFactory
import spray.http.{HttpRequest, MediaTypes}
import watchlistparser._
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.routing._

import scala.concurrent.Future
import scala.concurrent.duration._

import mailer._

case class Person(name: String, firstName: String, age: Int)

object JsonSupport extends DefaultJsonProtocol {
  implicit val KickassQueryFormat = jsonFormat3(KickassQuery.apply)
  implicit val RutrackerQueryFormat = jsonFormat3(RutrackerQuery.apply)
  implicit val AfishaQueryFormat = jsonFormat3(AfishaQuery.apply)
  implicit val ResultFormat = jsonFormat2(MovieQueryResult.apply)

  implicit val WatchlistQueryFormat = jsonFormat1(WatchListQuery)
  implicit val WatchlistResultFormat = jsonFormat1(MovieTitles)
}

object DockedServer extends App with SimpleRoutingApp {
  // setup
  implicit val actorSystem = ActorSystem()
  implicit val timeout = Timeout(60.second)
  val logger = Logger(LoggerFactory.getLogger("default"))
  import com.softwaremill.example.DockedServer.actorSystem.dispatcher

  // scheduling mailer
  val mailer = new Mailer()
//  actorSystem.scheduler.schedule(24 hour, 24 hour)(mailer.processWatchLists())

  val movieQueryActor = actorSystem.actorOf(Props(new QueryActor()).withRouter(RoundRobinRouter(1)), "moviequery")
  //  val movieQueryActor = actorSystem.actorOf(Props(new QueryActor()))

  startServer(interface = "0.0.0.0", port = 8080) {
    import com.softwaremill.example.JsonSupport._

    path("search" / "rutracker") {
      post {
        entity(as[RutrackerQuery]) { query =>
          val result: Future[MovieQueryResult] =
            (movieQueryActor ? Query(query)).mapTo[MovieQueryResult]

          complete(result)
        }
      }
    } ~
    path("search" / "kickass") {
      post {
        entity(as[KickassQuery]) { query =>
          val result: Future[MovieQueryResult] =
            (movieQueryActor ? Query(query)).mapTo[MovieQueryResult]

          complete(result)
        }
      }
    } ~
    path("search" / "afisha") {
      post {
        entity(as[AfishaQuery]) { query =>
          val result: Future[MovieQueryResult] =
            (movieQueryActor ? Query(query)).mapTo[MovieQueryResult]

          complete(result)
        }
      }
    } ~
    path("watchlist" / "imdb") {
      post {
        entity(as[WatchListQuery]) { query =>
          val result = new Parser().parse(query.link)

          complete(result)
        }
      }
    } ~
    path("process-wl") {
      get {
        respondWithMediaType(MediaTypes.`text/plain`) {
          entity(as[HttpRequest]) {
            obj =>
              logger.info("process-wl called..")
//              mailer.processWatchLists()
              complete { "OK" }
          }
        }
      }
    }
  }
}
