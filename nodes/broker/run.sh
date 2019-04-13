#!/bin/bash

docker build -t moquette_build -f Dockerfile.build . && \
docker run -v moquette-dist:/moquette -v gradle:/root/.gradle moquette_build && \
docker build -t moquette -f Dockerfile . && \
docker run -it --rm --name moquette \
    -v moquette-dist:/moquette \
    -v $(pwd)/config:/moquette/config \
    -p 2884:1884 \
    -p 2883:1883 \
    moquette 