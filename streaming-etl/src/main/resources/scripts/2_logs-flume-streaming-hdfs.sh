#!/usr/bin/env bash
## 1、将jar包scala-library_2.11.8.jar(这里一定要注意flume的classpath下是否还有其他版本的scala，
# 要是有的话，则删掉，用这个，一般会有，因为flume依赖kafka，kafka依赖scala)、
## commons-lang3-3.5.jar、spark-streaming-flume-sink_2.11-2.2.0.jar
放置在starl上的/root/dw-course/streaming-etl/apache-flume-1.8.0-bin/lib下

## 2、启动flume的agent
/opt/apache-flume-1.8.0-bin/bin/flume-ng agent --conf /opt/apache-flume-1.8.0-bin/conf --conf-file /root/dw-course/streaming-etl/flume/taildir_spark_streaming-flume-conf.properties --name agent1

## 3、启动Spark Streaming应用
 ./spark-submit --class com.dw.UserActionStreamingEtl \
 --deploy-mode client \
 --driver-memory 512m \
 --executor-memory 512m \
 --total-executor-cores 4 \
 --executor-cores 2 \
 /root/dw-course/streaming-etl/lib/streaming-etl-1.0-SNAPSHOT-jar-with-dependencies.jar \
 starl 44446

## 注意：也可以在本地运行Spark应用

## 4、创建一张hive表
hive -e "CREATE EXTERNAL TABLE IF NOT EXISTS streaming_etl.user_action_spark_partition (
    hostname STRING,
    userId STRING,
    actionTime STRING,
    clientIp STRING,
    browserName STRING,
    browserCode STRING,
    browserUserAgent STRING,
    currentLang STRING,
    screenWidth INT,
    screenHeight INT,
    browserType STRING,
    browserVersion STRING,
    platformType STRING,
    platformSeries STRING,
    platformVersion STRING
)
PARTITIONED BY (year INT, month INT, day INT)
STORED AS PARQUET
LOCATION 'hdfs://starl:9000/user/dw-course/streaming-etl/user-action-parquet/';"

hive -e "ALTER TABLE user_action_spark_partition ADD PARTITION (year=2018, month=201806, day=20180613);"
hive -e "ALTER TABLE user_action_spark_partition ADD PARTITION (year=2018, month=201806, day=20180607);"

## 5、使用Hive合并小文件
hive -e "CREATE EXTERNAL TABLE IF NOT EXISTS streaming_etl.user_action_spark_partition_merged (
    hostname STRING,
    userId STRING,
    actionTime STRING,
    clientIp STRING,
    browserName STRING,
    browserCode STRING,
    browserUserAgent STRING,
    currentLang STRING,
    screenWidth INT,
    screenHeight INT,
    browserType STRING,
    browserVersion STRING,
    platformType STRING,
    platformSeries STRING,
    platformVersion STRING
)
PARTITIONED BY (year INT, month INT, day INT)
STORED AS PARQUET;"

## --开启合并小文件的功能
hive -e "set hive.merge.mapredfiles=true;"
## --如果任何一个map job的输出文件的大小小于这个参数，则会触发另一个map job来合并小文件，默认是16000000字节
hive -e "set hive.merge.smallfiles.avgsize=5120;"
## --最终合并的文件的大小是hive.merge.size.per.task来控制，默认是256000000字节
hive -e "set hive.merge.size.per.task=256000000;"
hive -e "INSERT OVERWRITE TABLE user_action_spark_partition_merged PARTITION (year = 2018, month = 201806, day = 20180613)
SELECT hostname, userId, actionTime, clientIp, browserName, browserCode, browserUserAgent, currentLang,
screenWidth, screenHeight, browserType, browserVersion, platformType, platformSeries, platformVersion
FROM user_action_spark_partition WHERE day = 20180613;"

## 6、使用Impala合并小文件
impala-shell -e "set num_nodes=1;"
impala-shell -e "INSERT OVERWRITE TABLE user_action_spark_partition_merged PARTITION (year = 2018, month = 201806, day = 20180613)
SELECT hostname, userId, actionTime, clientIp, browserName, browserCode, browserUserAgent, currentLang,
screenWidth, screenHeight, browserType, browserVersion, platformType, platformSeries, platformVersion
FROM user_action_spark_partition WHERE day = 20180613;"
