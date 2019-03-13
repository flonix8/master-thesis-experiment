import argparse
import socket

import paho.mqtt.client as mqtt

parser = argparse.ArgumentParser()
parser.add_argument('-b', '--broker-host', help='Broker host to connect to', required=True)
args = parser.parse_args()


def messages(message_count: int) -> str:
    for message_id in range(message_count):
        yield f'Message #{message_id}, Client machine: {socket.gethostname()}'


def on_connect(client: mqtt.Client, userdata, flags, result_code):
    print(f'Connected with result code: {result_code}')

    for message in messages(2000):
        print(f'Publishing message: {message}')
        result = mqtt.MQTT_ERR_UNKNOWN
        while not result == mqtt.MQTT_ERR_SUCCESS:
            msg_info = client.publish('Test', message, qos=1)
            result = msg_info.rc

    client.disconnect()
    client.loop_stop()


def on_message(client: mqtt.Client, userdata, msg: mqtt.MQTTMessage):
    print(f'Received on topic "{msg.topic}: {msg.payload}"')


client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect(args.broker_host)
client.loop_forever()
