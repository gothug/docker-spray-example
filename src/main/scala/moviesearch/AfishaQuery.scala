package moviesearch

import org.openqa.selenium.firefox.FirefoxDriver
import org.jsoup.Jsoup
import java.net.URLEncoder

/**
 * Created by kojuhovskiy on 01/02/15.
 */
case class AfishaQuery(title: String, titleRus: Option[String], year: Int) extends MovieQuery {
  def doQuery(firefoxDriver: Option[FirefoxDriver] = None): MovieQueryResult = {
    val searchUrl = "http://www.afisha.ru/Search/?Search_str=%s"

    val query = searchUrl.format(titleRus.getOrElse(title))

    val html = Jsoup.connect(query).get()

    val list = html.body().getElementsByClass("b-search-page").get(0)

    val href = list.getElementsByClass("places-list-item").get(0).getElementsByTag("a").get(0).attr("href")
    val sourceHtml = html.html()

    MovieQueryResult(href, sourceHtml)
  }
}

object AfishaQuery {
  def main(args: Array[String]) = {
    val result = AfishaQuery("leviathan", Some("Левиафан"), 2014).doQuery()

    println(result)
  }
}
