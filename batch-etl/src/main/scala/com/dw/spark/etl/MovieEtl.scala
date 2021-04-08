package com.dw.spark.etl

import java.util.Properties

import org.apache.spark.sql.SparkSession

/**
spark-submit --class com.twq.spark.etl.MovieEtl \
--master spark://master:7077 \
--deploy-mode client \
--driver-memory 512m \
--executor-memory 512m \
--total-executor-cores 2 \
--executor-cores 1 \
--jars /home/hadoop-twq/dw-course/mysql-connector-java-5.1.44-bin.jar \
/home/hadoop-twq/dw-course/batch-etl-1.0-SNAPSHOT.jar
  本地运行的条件：
  1、hadoop的配置文件core-site.xml和hdfs-site.xml以及hive的配置文件hive-site.xml必须放在工程的resources下
  2、spark应用需要设置local模式
  3、pom.xml中需要依赖mysql的jdbc的驱动jar包
  */
object MovieEtl {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("MovieEtl")
      .enableHiveSupport()
      .master("local")
      .getOrCreate()

    val url = "jdbc:mysql://starl:3306/movie"
    val table = "movie"
    val columnName = "id"
    val lowerBound = 100
    val upperBound = 1000
    val numPartitions = 3
    val connectionProperties = new Properties()
    connectionProperties.put("user", "root")
    connectionProperties.put("password", "root")

    spark.read.jdbc(url, table, columnName, lowerBound, upperBound,
      numPartitions, connectionProperties)
      .write
      .saveAsTable("movielens.movie_spark")

    spark.stop()
  }
}
