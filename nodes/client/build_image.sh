#!/bin/bash

cd mqttBenchmarkClient
mvn package
cd ..

docker build -t flonix8/mqttbenchclient_pub -f Dockerfile_pub .
docker build -t flonix8/mqttbenchclient_sub -f Dockerfile_sub .

docker push flonix8/mqttbenchclient_pub
docker push flonix8/mqttbenchclient_sub