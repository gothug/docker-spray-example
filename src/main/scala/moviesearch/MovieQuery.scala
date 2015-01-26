package moviesearch

import org.openqa.selenium.firefox.FirefoxDriver

/**
 * Created by kojuhovskiy on 21/01/15.
 */

//object Language extends Enumeration {
//  type Lang = Value
//  val Rus, Eng = Value
//}

//case class Title(value: String, lang: Language.Lang)

//case class Query(title: String, titleRus: Option[String], year: Int)
case class Result(link: String, html: String)

trait MovieQuery {
  val title: String
  val titleRus: Option[String]
  val year: Int

  def doQuery(firefoxDriver: Option[FirefoxDriver] = None): Result
}
