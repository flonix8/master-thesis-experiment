#!/bin/bash

docker build -t broker .
docker run -d --rm --name broker --network net -v $(pwd)/config:/moquette/config broker
docker exec -it broker bash