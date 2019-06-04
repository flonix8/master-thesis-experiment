#!/usr/bin/env python

import sys

import networkx as nx
import yaml
from networkx.exception import NetworkXNoCycle, NetworkXNoPath

from common import get_path_delay_between_machines
from topologies import *

g = nx.Graph()

debug_topology(g)

# Check for cycles (lets not allow cycles for now)
try:
    nx.find_cycle(g)
    print("Cycle found! Aborting...")
    sys.exit(1)
except NetworkXNoCycle:
    pass

# Check if graph is connected
try:
    nx.is_connected(g)
except NetworkXNoPath:
    print("Graph is not connected! Aborting...")
    sys.exit(1)

# Build topology data

nodes = []

machines = list(filter(lambda n: n[1]['type'] == 'machine', g.nodes.data()))

for node, attrs in machines:
    data = {
        'name': node,
        **attrs,
        'delay_paths': []
    }
    for dst_node, dst_attrs in filter(lambda n: n[0] != node, machines):
        data['delay_paths'].append({
            'target': dst_node,
            'internal_ip': dst_attrs['internal_ip'],
            'value': get_path_delay_between_machines(g, node, dst_node)
        })
    nodes.append(data)

topo = {
    'nodes': nodes
}

# Write yaml file
with open('testbed_topology.yml', 'w') as file:
    file.write(yaml.dump(topo, default_flow_style=False, sort_keys=False, explicit_start=True))
