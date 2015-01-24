package com.softwaremill.example

import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import org.openqa.selenium.firefox.FirefoxDriver
import spray.can.Http
import spray.http.HttpMethods._
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling._
import spray.json.DefaultJsonProtocol
import spray.routing._

import scala.concurrent.Future
import scala.concurrent.duration._

import moviesearch._

case class Person(name: String, firstName: String, age: Int)

object QueryJsonSupport extends DefaultJsonProtocol {
  implicit val QueryFormat = jsonFormat3(Query)
}

object ResultJsonSupport extends DefaultJsonProtocol {
  implicit val ResultFormat = jsonFormat2(Result)
}

object DockedServer extends App with SimpleRoutingApp {
  // setup
  implicit val actorSystem = ActorSystem()
  implicit val timeout = Timeout(5.second)
  import com.softwaremill.example.DockedServer.actorSystem.dispatcher

  // an actor which holds a map of counters which can be queried and updated
  val countersActor = actorSystem.actorOf(Props(new CountersActor()))
  
  val firefoxDriver = initFirefoxDriver()
  
  def initFirefoxDriver(): FirefoxDriver = {
    val firefoxDriver: FirefoxDriver = new FirefoxDriver

    val url = "http://rutracker.org/forum/index.php"

    firefoxDriver.get(url)

    firefoxDriver.findElementByName("login_username").sendKeys("Greg89754")
    firefoxDriver.findElementByName("login_password").sendKeys("parol123")
    firefoxDriver.findElementByName("login").click()
    
    firefoxDriver
  }

  startServer(interface = "0.0.0.0", port = 8080) {
    import QueryJsonSupport._
    import ResultJsonSupport._
    
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
//          (IO(Http) ? HttpRequest(GET, Uri("https://kickass.so/usearch/фонтан"))).mapTo[HttpResponse]

        val bob = Person("Bob", "Parr", 32)

        complete {
//          bob
          response
        }
      } ~
      post {
        entity(as[Query]) { query =>
//          val logger = Logger(LoggerFactory.getLogger("name"))
//          logger.debug(x.toSt )
//          complete("OK")
//          complete(s"Person: ${person.title} - favorite number: ${person.year}")
          complete(RutrackerQuery.doQuery(query, Some(firefoxDriver)))
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
