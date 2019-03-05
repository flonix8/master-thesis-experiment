#!/bin/bash

docker build -t broker .
docker run -it --rm --name broker --network net -v $(pwd)/config:/moquette/config broker bash