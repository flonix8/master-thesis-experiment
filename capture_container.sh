#!/bin/bash

START_TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%S")
CAPTURE_TARGET=$2
CAPTURE_CONTAINER=$2.capture
COMMAND=$1
CAPTURE_FILENAME=${CAPTURE_TARGET}_${START_TIMESTAMP}.tcpdump

start() {
    docker build -t tcpdump ./tcpdump
    docker run -d \
        --network container:$CAPTURE_TARGET \
        --name $CAPTURE_CONTAINER \
        -v $(pwd)/capture:/capture \
        tcpdump -i eth0 -w /capture/$CAPTURE_FILENAME -U
    wireshark $(pwd)/capture/$CAPTURE_FILENAME & 
}

stop() {
    docker kill $CAPTURE_CONTAINER 1>/dev/null 2>&1
    docker rm $CAPTURE_CONTAINER 1>/dev/null 2>&1
}

stop_all() {
    docker rm --force $(docker ps -aq -f name=.capture) 1>/dev/null 2>&1
}

case "$COMMAND" in
"start")
    stop
    start
    ;;
"stop")
    stop
    ;;
"stop-all")
    stop_all
    ;;
*)
    echo \
"Usage:
    start <container_name>
    stop <container_name>
    stop-all"
esac