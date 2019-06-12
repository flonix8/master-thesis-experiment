#!/bin/sh
COMMAND=$1

if [ "$(docker ps -q -f ancestor=flonix8/mqttbenchclient_pub | wc -l)" -eq 1 ]
    then docker exec $(docker ps -q -f ancestor=flonix8/mqttbenchclient_pub) sh -c "echo $COMMAND > /mqttbenchclient/PublishingClient_command_input"
fi