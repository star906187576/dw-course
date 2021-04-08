#!/usr/bin/env bash
## 启动zookeeper
## 启动kafka
cd ~/bigdata/kafka_2.11-1.0.0
nohup bin/kafka-server-start.sh config/server.properties >logs/server.log 2>&1 &

## 创建topic user-action
bin/kafka-topics.sh --create --zookeeper starl:2181 --replication-factor 1 --partitions 2 --topic user-action

## 启动一个kafka consumer
bin/kafka-console-consumer.sh --bootstrap-server starl:9092 --topic user-action --from-beginning


## 启动log-webServer
java -DLOG_HOME="/root/dw-course/streaming-etl/logs/daily3"  \
-jar /root/dw-course/streaming-etl/lib/log-webServer-2.0-SNAPSHOT.jar \
--server.port=8090 \
--logging.config=/root/dw-course/streaming-etl/logback/daily_logback.xml &

##

