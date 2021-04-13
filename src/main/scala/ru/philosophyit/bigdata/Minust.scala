package ru.philosophyit.bigdata

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.{By, WebDriver, WebElement}
import scala.collection.JavaConversions.asScalaBuffer


trait Minust {

  System.setProperty("webdriver.chrome.driver", "chromedriver.exe")

  val options: ChromeOptions = new ChromeOptions()
  options.addArguments("--no-sandbox")
  options.addArguments("--headless")
  options.addArguments("--disable-dev-shm-usage")
  options.addArguments("--disable-gpu")

  implicit val driver: WebDriver = new ChromeDriver(options)

  implicit val spark: SparkSession = SparkSession.builder()
    .appName("par")
    .master("local")
    .getOrCreate()

  def init(): WebElement = {
    driver.get("http://unro.minjust.ru/NKOForeignAgent.aspx")
    driver.findElement(By.xpath("//*[@id=\"b_refresh\"]")).click()
    driver.findElement(By.xpath("//*[@id=\"pdg\"]/tbody"))
  }

  def getColumnsName(listAgents: WebElement): List[String] = {

    val listColums = listAgents.findElements(By.xpath("tr[2]")).toList
    val temp = for (e: WebElement <- listColums) yield e.findElements(By.tagName("th")).toList
    temp.map(listElem => listElem.filter { elem =>
      (!elem.getAttribute("width").equals("1") &&
        !elem.getAttribute("width").equals("26") &&
        !elem.getAttribute("width").equals("5"))
    }.map(_.getText)).flatten
      .map(elem => elem.replace("\n", " ")
        .replaceAll(" +", " ").trim)
  }

  def getQuantityPages(): Int = {
    driver.findElements(By.xpath("//*[@id=\"pdg\"]/tbody/tr[15]/td[2]/table/tbody/tr/td[3]"))
      .map(elem => elem.getText).toList(0)
      .split(" ").map(_.trim)
      .filter(elem => elem.length == 1).toList.max.toInt
  }

  def readPage(tagTable: WebElement, countColumns: Int, schema: StructType, page: String): DataFrame = {
    val listAgents = tagTable.findElements(By.tagName("tr")).toList
    val listAgentsId = for (e: WebElement <- listAgents) yield e.getAttribute("id")
    val listAttributeAgents = for (e: WebElement <- listAgents) yield e.findElements(By.tagName("td")).toList
    val listAttributeAgentPart1: List[List[String]] = listAttributeAgents.map(listElem => listElem.filter { elem =>
      elem.getAttribute("class").equals("pdg_item_left_even") ||
        elem.getAttribute("class").equals("pdg_item_even") ||
        elem.getAttribute("class").equals("pdg_item_left_odd") ||
        elem.getAttribute("class").equals("pdg_item_odd") ||
        elem.getAttribute("class").equals("pdg_item_right_even") ||
        elem.getAttribute("class").equals("pdg_item_right_odd")

    }.map(_.getText)).filter(_.length == countColumns)
    val listAttributeAgentPart2 = listAgentsId.filter(x => x.nonEmpty).map {
      case id => List(getAttributeValue( "trustee", id, page), getAttributeValue( "struct", id, page),
        getAttributeValue( "sources", id, page), getAttributeValue( "spendings", id, page),
        getAttributeValue( "purposes", id, page), getAttributeValue( "policy", id, page))
    }
    val listRows = listAttributeAgentPart1.zip(listAttributeAgentPart2)
      .map(list => list._1.dropRight(5) ::: list._2)
      .map(elem => Row.fromSeq(elem))
    spark.createDataFrame(spark.sparkContext.makeRDD(listRows), schema)
  }

  def getAttributeValue(nameAttribute: String, id: String, page: String): String = {
    val urlTable: String = s"http://unro.minjust.ru/PopUp.aspx?mode=${nameAttribute}&id=${id}"
    driver.get(urlTable)
    val tagTable = driver.findElement(By.xpath("//*[@id=\"depart_info\"]/tbody"))
    val attributeValue = tagTable.findElements(By.tagName("td")).toList.filter(e => e.getAttribute("class").equals("tab_val")).map(_.getText).mkString(" ")
    driver.get("http://unro.minjust.ru/NKOForeignAgent.aspx")
    driver.findElement(By.xpath("//*[@id=\"b_refresh\"]")).click()
    driver.findElement(By.xpath(page)).click()
    attributeValue
  }

  def quit(): Unit = {
    spark.stop()
    driver.close()
    driver.quit()
  }
}
