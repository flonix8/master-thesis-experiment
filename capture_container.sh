#!/bin/bash

START_TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%S")
CAPTURE_TARGET=$1
CAPTURE_CONTAINER=$1.capture
COMMAND=$2
CAPTURE_FILENAME=${CAPTURE_TARGET}_${START_TIMESTAMP}.tcpdump

start() {
    docker build -t tcpdump ./tcpdump > /dev/null
    docker run -d \
        --network container:$CAPTURE_TARGET \
        --name $CAPTURE_CONTAINER \
        -v $(pwd)/capture:/capture \
        tcpdump -i eth0 -w /capture/$CAPTURE_FILENAME -U
    wireshark $(pwd)/capture/$CAPTURE_FILENAME
}

stop() {
    docker kill $CAPTURE_CONTAINER
    docker rm $CAPTURE_CONTAINER
}

case "$COMMAND" in
"start")
    stop
    start    
    ;;
"stop")
    stop
    ;;
*)
    echo "
    Usage:
        <container_name> [start | stop]"
esac