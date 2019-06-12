#!/bin/sh
COMMAND=$1

if [ "$(docker ps -q -f ancestor=flonix8/mqttbenchclient_sub | wc -l)" -eq 1 ]
    then docker exec $(docker ps -q -f ancestor=flonix8/mqttbenchclient_sub) sh -c "echo $COMMAND > /mqttbenchclient/SubscribingClient_command_input"
fi