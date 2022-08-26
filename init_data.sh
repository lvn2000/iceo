#!/bin/sh

docker cp ./src/main/resources/generate_data.sql postgres:generate_data.sql
docker exec postgres psql -h localhost -p 5432 -U postgres -f generate_data.sql

docker exec kafka kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic test
