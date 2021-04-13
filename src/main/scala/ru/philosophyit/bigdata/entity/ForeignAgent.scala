package ru.philosophyit.bigdata.entity

import org.apache.spark.sql.types.{StringType, StructField, StructType}

object ForeignAgent extends Enumeration {

  val name, address, emptycolumn, ogrn, accounting,
  registry, inclusion_in_register, exclusion_from_registry,
  reason_for_exclusion, leader, structure, sources, expenditure,
  objectives_of_activity, activities = Value


  val schema: StructType = StructType(
    Seq(
      StructField(name.toString, StringType),
      StructField(address.toString, StringType),
      StructField(emptycolumn.toString, StringType),
      StructField(ogrn.toString, StringType),
      StructField(accounting.toString, StringType),
      StructField(registry.toString, StringType),
      StructField(inclusion_in_register.toString, StringType),
      StructField(exclusion_from_registry.toString, StringType),
      StructField(reason_for_exclusion.toString, StringType),
      StructField(leader.toString, StringType),
      StructField(structure.toString, StringType),
      StructField(sources.toString, StringType),
      StructField(expenditure.toString, StringType),
      StructField(objectives_of_activity.toString, StringType),
      StructField(activities.toString, StringType)
    )
  )

}
