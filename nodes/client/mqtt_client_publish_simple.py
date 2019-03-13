import socket
from paho.mqtt.publish import multiple


def messages(message_count: int) -> str:
    for message_id in range(message_count):
        yield {
            'topic': 'Test',
            'payload': f'Message #{message_id}, Client machine: {socket.gethostname()}',
            'qos': 0
        }


multiple(messages(1000), 'broker')
