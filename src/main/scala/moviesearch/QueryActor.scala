package moviesearch

import akka.actor.Actor
import org.openqa.selenium.firefox.FirefoxDriver

/**
 * Created by kojuhovskiy on 25/01/15.
 */

case class Query(query: MovieQuery)

class QueryActor(firefoxDriver: Option[FirefoxDriver] = None) extends Actor {
  override def receive = {
    case Query(query) => sender ! query.doQuery(firefoxDriver)
  }
}
