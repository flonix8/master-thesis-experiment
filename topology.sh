#!/bin/bash

build() {
    docker build -t containernet ./containernet
}

start() {
    docker run --name containernet -it --rm --privileged --pid='host' \
        -v /var/run/docker.sock:/var/run/docker.sock \
        -v $(pwd)/topology:/containernet/topology \
        containernet "$@"
}

stop() {
    if [ $(docker ps -aq | wc -l) -eq 0 ]; then 
        echo "Nothing to clean up..."
        exit
    fi
    docker stop $(docker ps -aq)
    docker container prune --force
}

case "$1" in
"build")
    build
    ;;
"start")
    start "${@:2}"
    stop
    ;;
"stop")
    stop
    ;;
*)
    echo "
    Usage:
        build | start | stop"
    ;;
esac