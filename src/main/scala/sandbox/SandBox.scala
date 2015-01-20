package sandbox

import org.jsoup.Jsoup

/**
 * Created by kojuhovskiy on 19/01/15.
 */
class SandBox {

}

object SandBox {
  def main(args: Array[String]) = {

    val htmlString: String = "<!DOCTYPE html>" ++
      "<html>" ++
      "<head>" ++
      "<title>JSoup Example</title>" ++
      "</head>" ++
      "<body>" ++
      "<table><tr><td><h1>HelloWorld</h1></tr>" ++
      "</table>" ++
      "</body>" ++
      "</html>"

//    val html = Jsoup.parse(htmlString)

    val html = Jsoup.connect("https://kickass.so/usearch/leviathan%202014/").get()

    val torrents = html.body().getElementsByClass("torrentname")

    val href = torrents.get(0).getElementsByTag("a").get(0).attr("href")
    val listHtml = html.html()

    println("hello world!")
  }
}
