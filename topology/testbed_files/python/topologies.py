from networkx.classes import Graph

from common import node_attrs, edge_attrs


def reference_topology(g: Graph):
    # Cloud zone #1
    g.add_node('cloud1', **node_attrs(type='zone'))
    g.add_node('cloud1_client1', **node_attrs())
    g.add_node('cloud1_broker1', **node_attrs())

    g.add_edge('cloud1_broker1', 'cloud1', **edge_attrs(delay=2))
    g.add_edge('cloud1_client1', 'cloud1', **edge_attrs(delay=2))

    # Edge zone #1
    g.add_node('edge1', **node_attrs(type='zone'))
    g.add_node('edge1_client1', **node_attrs())
    g.add_node('edge1_client2', **node_attrs())
    g.add_node('edge1_broker1', **node_attrs())
    g.add_node('edge1_broker2', **node_attrs())
    g.add_node('edge1_broker3', **node_attrs())
    g.add_node('edge1_broker4', **node_attrs())

    g.add_edge('edge1_client1', 'edge1', **edge_attrs(delay=2))
    g.add_edge('edge1_client2', 'edge1', **edge_attrs(delay=2))
    g.add_edge('edge1_broker1', 'edge1', **edge_attrs(delay=2))
    g.add_edge('edge1_broker2', 'edge1', **edge_attrs(delay=2))
    g.add_edge('edge1_broker3', 'edge1', **edge_attrs(delay=2))
    g.add_edge('edge1_broker4', 'edge1', **edge_attrs(delay=2))

    # Edge zone #2
    g.add_node('edge2', **node_attrs(type='zone'))
    g.add_node('edge2_client1', **node_attrs())
    g.add_node('edge2_broker1', **node_attrs())
    g.add_node('edge2_broker2', **node_attrs())
    g.add_node('edge2_broker3', **node_attrs())
    g.add_node('edge2_broker4', **node_attrs())

    g.add_edge('edge2_client1', 'edge2', **edge_attrs(delay=2))
    g.add_edge('edge2_broker1', 'edge2', **edge_attrs(delay=2))
    g.add_edge('edge2_broker2', 'edge2', **edge_attrs(delay=2))
    g.add_edge('edge2_broker3', 'edge2', **edge_attrs(delay=2))
    g.add_edge('edge2_broker4', 'edge2', **edge_attrs(delay=2))

    # Connect zones
    g.add_edge('edge1', 'cloud1', **edge_attrs(delay=20))
    g.add_edge('edge2', 'cloud1', **edge_attrs(delay=20))


def debug_topology(g: Graph):
    # Cloud zone #1
    g.add_node('cloud1', **node_attrs(type='zone'))
    g.add_node('cloud1_client1', **node_attrs())
    g.add_node('cloud1_broker1', **node_attrs())

    g.add_edge('cloud1_broker1', 'cloud1', **edge_attrs(delay=2))
    g.add_edge('cloud1_client1', 'cloud1', **edge_attrs(delay=2))

    # Edge zone #1
    g.add_node('edge1', **node_attrs(type='zone'))
    g.add_node('edge1_client1', **node_attrs())
    g.add_node('edge1_broker1', **node_attrs())

    g.add_edge('edge1_client1', 'edge1', **edge_attrs(delay=2))
    g.add_edge('edge1_broker1', 'edge1', **edge_attrs(delay=2))

    # Connect zones
    g.add_edge('edge1', 'cloud1', **edge_attrs(delay=20))
