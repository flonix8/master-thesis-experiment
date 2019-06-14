#!/usr/bin/env python

import networkx as nx
import yaml
import sys

import topologies
from common import validate_graph, fill_node_attrs, resolve_names

g = nx.Graph()

# Generate topology

topologies.simple_bridge_local_sub(g)

# Process graph

validate_graph(g)

fill_node_attrs(g)

resolve_names(g)

# Build topology data

topo = {
    'nodes': [attrs for node, attrs in g.nodes(data=True) if attrs['type'] == 'machine']
}

# Write yaml file
with open(f'{sys.path[0]}/../testbed_topology.yml', 'w') as file:
    file.write(yaml.dump(topo, default_flow_style=False, sort_keys=False, explicit_start=True))

# Show graph for a quick sanity check
# nx.draw_networkx(g)
# plt.show()
