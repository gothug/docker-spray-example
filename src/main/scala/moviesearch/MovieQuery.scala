package moviesearch

import com.typesafe.scalalogging.slf4j.Logger
import org.openqa.selenium.firefox.FirefoxDriver
import org.slf4j.LoggerFactory

/**
 * Created by kojuhovskiy on 21/01/15.
 */

//object Language extends Enumeration {
//  type Lang = Value
//  val Rus, Eng = Value
//}

//case class Title(value: String, lang: Language.Lang)

//case class Query(title: String, titleRus: Option[String], year: Int)
case class MovieQueryResult(link: Option[String])

trait MovieQuery {
  val title: String
  val titleRus: Option[String]
  val year: Int

  def doQuery(firefoxDriver: Option[FirefoxDriver] = None): MovieQueryResult

  def logInfo(info: String) = Logger(LoggerFactory.getLogger(this.getClass.getName)).info(info)
}
