package moviesearch

import akka.actor.Actor
import org.openqa.selenium.firefox.FirefoxDriver

/**
 * Created by kojuhovskiy on 25/01/15.
 */

case class Query(query: MovieQuery)

class QueryActor extends Actor {
  val firefoxDriver: FirefoxDriver = initFirefoxDriver()
  
  override def receive = {
    case Query(query) => sender ! query.doQuery(Some(firefoxDriver))
//    case Get(counterName) => sender ! counters.getOrElse(counterName, 0)
//    case Add(counterName, amount) =>
//      val newAmount = counters.getOrElse(counterName, 0) + amount
//      counters = counters + (counterName -> newAmount)
  }

  def initFirefoxDriver(): FirefoxDriver = {
    val firefoxDriver: FirefoxDriver = new FirefoxDriver

    val url = "http://rutracker.org/forum/index.php"

    firefoxDriver.get(url)

    firefoxDriver.findElementByName("login_username").sendKeys("Greg89754")
    firefoxDriver.findElementByName("login_password").sendKeys("parol123")
    firefoxDriver.findElementByName("login").click()

    firefoxDriver
  }
}
