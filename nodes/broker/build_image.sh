#!/bin/bash
docker rm --force moquette-build || true && \
docker build -t moquette-build_base -f Dockerfile.build . && \
docker run -v gradle-cache:/root/.gradle --name moquette-build moquette-build_base && \
docker commit moquette-build moquette-build && \
docker build -t moquette -t flonix8/moquette -f Dockerfile .