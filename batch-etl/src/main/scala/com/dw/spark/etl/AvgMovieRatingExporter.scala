package com.dw.spark.etl

import java.util.Properties

import org.apache.spark.sql.{SaveMode, SparkSession}


object AvgMovieRatingExporter {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("MovieEtl")
      .enableHiveSupport()
      .getOrCreate()

    val url = "jdbc:mysql://starl:3306/movie"
    val table = "avg_movie_rating"
    val connectionProperties = new Properties()
    connectionProperties.put("user", "root")
    connectionProperties.put("password", "root")

    spark.read.table("movielens.avg_movie_rating")
      .write.mode(SaveMode.Overwrite)
      .jdbc(url, table, connectionProperties)

    spark.stop()
  }
}
