#!/bin/bash

zookeeper_port=2181
kafka_port=9092

echo "Killing cassandra ..."
docker kill cassandra
docker rm cassandra

echo "Killing kafka ..."
docker kill kafka
docker rm kafka

echo "Starting Kafka ..."
docker run -d -p $zookeeper_port:$zookeeper_port -p $kafka_port:$kafka_port --env ADVERTISED_HOST=`boot2docker ip` --env ADVERTISED_PORT=$kafka_port --name kafka spotify/kafka

echo "Starting Cassandra ..."
docker run -d --name cassandra spotify/cassandra

echo "Done."
