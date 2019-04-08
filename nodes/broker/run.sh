#!/bin/bash

docker build -t moquette_build -f Dockerfile.build . && \
docker run -v moquette-dist:/moquette -v gradle:/root/.gradle moquette_build && \
docker build -t moquette -f Dockerfile . && \
docker run -d --rm --name moquette \
    -v moquette-dist:/moquette \
    -v $(pwd)/config:/moquette/config \
    -p 1884:1883 \
    moquette && \
docker logs -f moquette