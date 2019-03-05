import argparse

import paho.mqtt.client as mqtt

parser = argparse.ArgumentParser()
parser.add_argument('-b', '--broker-host', help='Broker host to connect to', required=True)
args = parser.parse_args()


def on_connect(client: mqtt.Client, userdata, flags, result_code):
    print(f'Connected with result code: {result_code}')
    client.subscribe('#')


def on_message(client: mqtt.Client, userdata, msg: mqtt.MQTTMessage):
    print(f'Received on topic "{msg.topic}: {msg.payload}"')


client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect(args.broker_host)
client.loop_forever()
