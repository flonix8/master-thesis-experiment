import random
import sys

import networkx as nx
from networkx import Graph

# .1, .2, .3, .255 are reserved by AWS
ip_address_pool = ['10.0.2.' + str(i) for i in range(4, 255)]


def generate_random_ip():
    if len(ip_address_pool) == 0:
        print('IP address space exhausted! Aborting...')
        sys.exit(1)
    random_ip = random.choice(ip_address_pool)
    ip_address_pool.remove(random_ip)
    return random_ip


def node_attrs(**kwargs):
    attrs = {
        'type': 'machine',
        'flavor': 't3.nano',
        'bandwidth_out': 10000,
        'internal_ip': generate_random_ip()
    }
    for k, v in kwargs.items():
        attrs[k] = v
    return attrs


def edge_attrs(**kwargs):
    attrs = {
        'delay': 0,
    }
    for k, v in kwargs.items():
        attrs[k] = v
    return attrs


def get_path_delay_between_machines(graph: Graph, src, dst):
    return nx.shortest_path_length(graph, source=src, target=dst, weight='delay')
