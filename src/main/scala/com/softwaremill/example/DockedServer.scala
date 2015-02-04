package com.softwaremill.example

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.routing.RoundRobinRouter
import akka.util.Timeout
import moviesearch._
import watchlistparser._
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.routing._

import scala.concurrent.Future
import scala.concurrent.duration._

case class Person(name: String, firstName: String, age: Int)

object JsonSupport extends DefaultJsonProtocol {
  implicit val KickassQueryFormat = jsonFormat3(KickassQuery.apply)
  implicit val RutrackerQueryFormat = jsonFormat3(RutrackerQuery.apply)
  implicit val AfishaQueryFormat = jsonFormat3(AfishaQuery.apply)
  implicit val ResultFormat = jsonFormat2(Result)

  implicit val WatchlistQueryFormat = jsonFormat1(WatchListQuery)
  implicit val WatchlistResultFormat = jsonFormat1(MovieTitles)
}

object DockedServer extends App with SimpleRoutingApp {
  // setup
  implicit val actorSystem = ActorSystem()
  implicit val timeout = Timeout(60.second)
  import com.softwaremill.example.DockedServer.actorSystem.dispatcher

  // an actor which holds a map of counters which can be queried and updated
  val countersActor = actorSystem.actorOf(Props(new CountersActor()))

//  val movieQueryActor = actorSystem.actorOf(Props(new QueryActor()))

  val movieQueryActor = actorSystem.actorOf(Props(new QueryActor()).withRouter(RoundRobinRouter(1)), "moviequery")

//  val firefoxDriver = initFirefoxDriver()

  startServer(interface = "0.0.0.0", port = 8080) {
    import com.softwaremill.example.JsonSupport._

    // simplest route, matching only GET /hello, and sending a "Hello World!" response
    get {
      path("hello") {
        complete {
          "Hello, 2 Universes!"
        }
      }
    } ~ // the ~ concatenates two routes: if the first doesn't match, the second is tried
    path("counter" / Segment) { counterName =>  // extracting the second path component into a closure argument
      get {
        complete {
          (countersActor ? Get(counterName)) // integration with futures
            .mapTo[Int]
            .map(amount => s"$counterName is: $amount")
        }
      } ~
      post {
        parameters("amount".as[Int]) { amount => // the type of the amount closure argument is Int, as specified!
          countersActor ! Add(counterName, amount) // fire-and-forget
          complete {
            "OK"
          }
        }
      }
    } ~
    path("search" / "rutracker") {
//      get {
//        val bob = Person("Bob", "Parr", 32)
//        complete {
//          bob
//        }
//      } ~
      post {
        entity(as[RutrackerQuery]) { query =>
          val result: Future[Result] =
            (movieQueryActor ? Query(query)).mapTo[Result]

          complete(result)
        }
      }
    } ~
    path("search" / "kickass") {
      post {
        entity(as[KickassQuery]) { query =>
          val result: Future[Result] =
            (movieQueryActor ? Query(query)).mapTo[Result]

          complete(result)
        }
      }
    } ~
    path("search" / "afisha") {
      post {
        entity(as[AfishaQuery]) { query =>
          val result: Future[Result] =
            (movieQueryActor ? Query(query)).mapTo[Result]

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
    }
  }

  // implementation of the actor
  class CountersActor extends Actor {
    private var counters = Map[String, Int]()

    override def receive = {
      case Get(counterName) => sender ! counters.getOrElse(counterName, 0)
      case Add(counterName, amount) =>
        val newAmount = counters.getOrElse(counterName, 0) + amount
        counters = counters + (counterName -> newAmount)
    }
  }

  // messages to communicate with the counters actor
  case class Get(counterName: String)
  case class Add(counterName: String, amount: Int)
}
