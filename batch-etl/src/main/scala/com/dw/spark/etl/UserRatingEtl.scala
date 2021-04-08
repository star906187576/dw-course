package com.dw.spark.etl

import java.util.Properties

import org.apache.spark.sql.SparkSession

/**
spark-submit --class com.twq.spark.etl.UserRatingEtl \
--master spark://master:7077 \
--deploy-mode client \
--driver-memory 512m \
--executor-memory 512m \
--total-executor-cores 2 \
--executor-cores 1 \
--jars /home/hadoop-twq/dw-course/mysql-connector-java-5.1.44-bin.jar \
/home/hadoop-twq/dw-course/batch-etl-1.0-SNAPSHOT.jar
  */
object UserRatingEtl {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("MovieEtl")
      .enableHiveSupport()
      .getOrCreate()

    val url = "jdbc:mysql://starl:3306/movie"
    val table = "user_rating"
    val predicates = Array("dt_time between '1970-01-11 00:00:00' and '1970-01-11 12:00:00'",
      "dt_time between '1970-01-11 12:00:01' and '1970-01-11 23:59:59'")
    val connectionProperties = new Properties()
    connectionProperties.put("user", "root")
    connectionProperties.put("password", "root")

    spark.read.jdbc(url, table, predicates, connectionProperties)
      .write
      .saveAsTable("movielens.user_rating_spark")

    spark.stop()
  }
}
