package com.softwaremill.example

import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.http.HttpMethods._
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling._
import spray.json.DefaultJsonProtocol
import spray.routing._

import scala.concurrent.Future
import scala.concurrent.duration._

case class Person(name: String, firstName: String, age: Int)

object PersonJsonSupport extends DefaultJsonProtocol {
  implicit val PortofolioFormats = jsonFormat3(Person)
}

object DockedServer extends App with SimpleRoutingApp {
  // setup
  implicit val actorSystem = ActorSystem()
  implicit val timeout = Timeout(1.second)
  import com.softwaremill.example.DockedServer.actorSystem.dispatcher

  // an actor which holds a map of counters which can be queried and updated
  val countersActor = actorSystem.actorOf(Props(new CountersActor()))

  startServer(interface = "0.0.0.0", port = 8080) {
    import PersonJsonSupport._
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
    path("request") {
      get {
        val response: Future[HttpResponse] =
          (IO(Http) ? HttpRequest(GET, Uri("http://google.com"))).mapTo[HttpResponse]

        val bob = Person("Bob", "Parr", 32)

        complete {
          bob
        }
      } ~
      post {
        entity(as[Person]) { person =>
//          val logger = Logger(LoggerFactory.getLogger("name"))
//          logger.debug(x.toSt )
//          complete("OK")
          complete(s"Person: ${person.name} - favorite number: ${person.age}")
        }
      }
    } // the ~ concatenates two routes: if the first doesn't match, the second is tried
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
