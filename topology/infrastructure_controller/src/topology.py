import networkx as nx

from nodes import Node


# Define nodes
nodes = [
    Node('edge1_client1'),
    Node('edge1_broker1'),
    Node('cloud1_client1'),
    Node('cloud1_broker1'),
]

# Create network
net = nx.Graph()
net.add_nodes_from([(node.name, node) for node in nodes])
