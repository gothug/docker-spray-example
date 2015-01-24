package moviesearch

import java.net.URLEncoder

import org.jsoup.Jsoup

import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import org.openqa.selenium.firefox.FirefoxDriver

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

import spray.can.Http
import spray.http._
import spray.http.HttpMethods._

/**
 * Created by kojuhovskiy on 21/01/15.
 */
object KickassQuery extends MovieQuery {
  def doQuery(query: Query, firefoxDriver: Option[FirefoxDriver] = None): Result = {
    val root = "https://kickass.so/usearch"
    
//    val queryUrl = s"${query.filmName} ${query.year}"
//    val encodedQueryUrl = URLEncoder.encode("фонтан", "UTF-8")
    val encodedQueryUrl = URLEncoder.encode("фонтан")
//    val encodedQueryUrl = URLEncoder.encode("leviathan 2014", "UTF-8")

//    val url = s"$root/$encodedQueryUrl"

    val url = "http://kickass.so/usearch/" + URLEncoder.encode("фонтан", "UTF-8")
//    val url = "http://www.afisha.ru/Search/?Search_str=" + URLEncoder.encode("левиафан", "UTF-8")
//    val url = "http://yandex.ru/yandsearch?text=" + URLEncoder.encode("мария два", "UTF-8")
//    val url = "http://yandex.ru/yandsearch?text=" + URLEncoder.encode("Mary", "UTF-8")

//    val url = "http://api.hostip.info/get_json.php?ip=12.215.42.19"
//    val result = scala.io.Source.fromURL("http://google.com")

//    val encodedUrl = URLEncoder.encode(url, "UTF-8")


//    val response: Future[HttpResponse] =
//      (IO(Http) ? HttpRequest(GET, Uri("http://google.com"))).mapTo[HttpResponse]
//    Await.result(response, 5 seconds)

    val html = Jsoup.connect(url).get()

    val torrents = html.body().getElementsByClass("torrentname")

    val href = torrents.get(0).getElementsByTag("a").get(0).attr("href")
    val listHtml = html.html()
    
    Result(href, listHtml)
  }
  
  def main(args: Array[String]) = {
//    val result = doQuery(Query("дневник памяти", 2004))
//    val result = doQuery(Query("leviathan", 2014))

    println("Hello world!")
  }
}
