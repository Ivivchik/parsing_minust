package ru.philosophyit.bigdata

import org.apache.spark.sql.{DataFrame, Row, SaveMode}
import org.openqa.selenium._
import ru.philosophyit.bigdata.entity.ForeignAgent

object Main extends Minust {

  def main(args: Array[String]): Unit = {

    val listAgents: WebElement = init()
    val countPages: Int = getQuantityPages()
    val countColumns: Int = getColumnsName(listAgents).length
    var result: DataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], ForeignAgent.schema)

    for (i <- 1 to countPages) {
      val xpathPage = "//*[@id=\"pdg\"]/tbody/tr[15]/td[2]/table/tbody/tr/td[3]/a[" + i + "]"
      driver.findElement(By.xpath(xpathPage)).click()
      result = result.union(readPage(driver.findElement(By.xpath("//*[@id=\"pdg\"]/tbody")), countColumns, ForeignAgent.schema, xpathPage))
    }

    result.select(
      "name", "address", "ogrn", "accounting",
      "registry", "inclusion_in_register", "exclusion_from_registry",
      "reason_for_exclusion", "leader", "structure", "sources", "expenditure",
      "objectives_of_activity", "activities"
    )
      .repartition(1)
      .write.option("header", true)
      .option("nullValue", "\\N")
      .option("delimiter", 	"\u001D")
      .mode(SaveMode.Append)
      .csv("/home/output")
    quit()
  }
}