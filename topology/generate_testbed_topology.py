import sys
import networkx as nx
from networkx.exception import NetworkXNoCycle, NetworkXNoPath
from networkx.classes.graph import Graph
import yaml


def node_attrs(**kwargs):
    attrs = {
        'type': 'machine',
        'flavor': 't3.nano',
    }
    for k, v in kwargs.items():
        attrs[k] = v
    return attrs


def edge_attrs(**kwargs):
    attrs = {
        'delay': 0,
        'bandwidth_out': 1000,
    }
    for k, v in kwargs.items():
        attrs[k] = v
    return attrs


def get_path_delay_between_machines(graph: Graph, src, dst):
    return nx.shortest_path_length(graph, source=src, target=dst, weight='delay')


# Topology definition

g = nx.Graph()
g.add_node('cloud1_client1', **node_attrs())
g.add_node('cloud1_broker1', **node_attrs())
g.add_node('edge1_client1', **node_attrs())
g.add_node('edge1_broker1', **node_attrs())
g.add_node('edge1', **node_attrs(type='zone'))
g.add_node('cloud1', **node_attrs(type='zone'))

g.add_edge('edge1', 'cloud1', **edge_attrs(delay=20))
g.add_edge('edge1_client1', 'edge1', **edge_attrs())
g.add_edge('edge1_broker1', 'edge1', **edge_attrs())
g.add_edge('cloud1_broker1', 'cloud1', **edge_attrs())
g.add_edge('cloud1_client1', 'cloud1', **edge_attrs())

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
        'delay_to': {}
    }
    for dst_node, _ in filter(lambda n: n[0] != node, machines):
        data['delay_to'][dst_node] = get_path_delay_between_machines(g, node, dst_node)
    nodes.append(data)

topo = {
    'nodes': nodes
}

# Write yaml file
print(yaml.dump(topo, default_flow_style=False, sort_keys=False))
