package com.dw

import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * 使用Spark SQL合并小文件
  */
object SmallFileMerger {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("SmallFileMerger")
      .master("local[4]")
      .getOrCreate()

    val inputPath = spark.conf.get("spark.small.file.merge.inputPath",
      "hdfs://starl:9000/user/hadoop-twq/dw-course/streaming-etl/user-action-parquet/year=2021/month=202104/day=20210402")

    val numberPartition = spark.conf.get("spark.small.file.merge.numberPartition", "2").toInt

    val outputPath = spark.conf.get("spark.small.file.merge.outputPath",
      "hdfs://starl:9000/user/hadoop-twq/dw-course/streaming-etl/user-action-merged/year=2021/month=202104/day=20210402")

    spark.read.parquet(inputPath)
      .repartition(numberPartition)
      //.coalesce(numberPartition)
      .write
      .mode(SaveMode.Overwrite)
      .parquet(outputPath)

    spark.stop()
  }
}
