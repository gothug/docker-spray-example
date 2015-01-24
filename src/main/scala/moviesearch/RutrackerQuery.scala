package moviesearch

import org.openqa.selenium.firefox.FirefoxDriver

/**
 * Created by kojuhovskiy on 22/01/15.
 */
object RutrackerQuery extends MovieQuery {
  def doQuery(query: Query, firefoxDriver: Option[FirefoxDriver] = None): Result = {
//    val driver = new PhantomJSDriver()
    val driver = firefoxDriver.getOrElse(new FirefoxDriver)
    
//        val driver = firefoxDriver

    val url = "http://rutracker.org/forum/index.php"

    driver.get(url)

    //    driver.findElementByName("login_username").sendKeys("Greg89754")
    //    driver.findElementByName("login_password").sendKeys("parol123")
    //    driver.findElementByName("login").click()

    val queryString = query.titleRus.getOrElse(query.title) + " " + query.year

    driver.findElementById("search-text").sendKeys(queryString)

    driver.findElementByXPath("//form[@id='quick-search']/input[@type='submit']").click()

    val downloadsSortElement = driver.findElementByXPath("//th[@title='Торрент скачан']")
    downloadsSortElement.click()
    downloadsSortElement.click()

    val rootXPath = "//table[@class='forumline tablesorter']"
    val filmListHTML = driver.findElementByXPath(rootXPath).getAttribute("innerHTML")

    val link =
      driver.findElementByXPath(
        rootXPath + "/tbody/tr/td[contains(@class, 't-title')]/div/a"
      ).getAttribute("href")

    //    driver.close()

    Result(link, filmListHTML)
  }
}
