from networkx.classes import Graph

from common import node_attrs, edge_attrs, sub_config, pub_config


def reference_topology(g: Graph):
    # Cloud nodes
    g.add_node('cloud1', **node_attrs(type='zone'))
    g.add_node('cloud1_client1', **node_attrs(role='client',
                                              client_config=[
                                                  pub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='cloud-to-edge/control-commands',
                                                      frequency=50,
                                                  ),
                                                  pub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='cloud-to-edge/config-updates',
                                                      frequency=10,
                                                      payload_size=10000,
                                                  ),
                                                  sub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='edge-to-cloud/#',
                                                  ),
                                                  sub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='edge-to-edge/data/#',
                                                  ),
                                              ]))
    g.add_node('cloud1_broker1', **node_attrs(role='broker'))
    # Edge nodes
    g.add_node('edge1_client1', **node_attrs(role='client',
                                             client_config=[
                                                 pub_config(
                                                     connect_to='edge1_broker1',
                                                     topic='edge-to-cloud/aggregated-data',
                                                     frequency=20,
                                                     start_offset=20,
                                                     runtime=40,
                                                     payload_size=500000,
                                                 ),
                                                 sub_config(
                                                     connect_to='edge1_broker1',
                                                     topic='cloud-to-edge/#',
                                                 ),
                                                 sub_config(
                                                     connect_to='edge1_broker1',
                                                     topic='edge-to-edge/data/#',
                                                 ),
                                             ]))
    g.add_node('edge1_client2', **node_attrs(role='client',
                                             client_config=[
                                                 sub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='cloud-to-edge/#',
                                                 ),
                                                 sub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='edge-to-edge/data/client3'
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='edge-to-edge/control-commands/client3',
                                                     frequency=20,
                                                 ),
                                             ]))
    g.add_node('edge1_client3', **node_attrs(role='client',
                                             client_config=[
                                                 sub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='edge-to-edge/control-commands/client3',
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='edge-to-edge/data/client3',
                                                     frequency=50,
                                                 ),
                                             ]))
    g.add_node('edge1_broker1', **node_attrs(role='broker'))
    g.add_node('edge1_broker2', **node_attrs(role='broker'))

    # Connect cloud
    g.add_edge('cloud1_broker1', 'cloud1', **edge_attrs(delay=2))
    g.add_edge('cloud1_client1', 'cloud1', **edge_attrs(delay=2))
    # Connect edge
    g.add_edge('edge1_client1', 'edge1_broker1', **edge_attrs(delay=2))
    g.add_edge('edge1_client2', 'edge1_broker2', **edge_attrs(delay=2))
    g.add_edge('edge1_client3', 'edge1_broker2', **edge_attrs(delay=2))
    g.add_edge('edge1_broker2', 'edge1_broker1', **edge_attrs(delay=2))

    # Connect to cloud
    g.add_edge('edge1_broker1', 'cloud1', **edge_attrs(delay=20))


def debug_topology(g: Graph):
    # Cloud zone #1
    g.add_node('cloud1', **node_attrs(type='zone'))

    g.add_node('cloud1_broker1', **node_attrs(role='broker'))
    g.add_node('cloud1_client1', **node_attrs(role='client',
                                              client_config=[
                                                  pub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='topic1',
                                                      frequency=100
                                                  ),
                                                  pub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='topic2',
                                                      frequency=10,
                                                      payload_size=2000
                                                  )]))
    g.add_node('cloud1_client2', **node_attrs(role='client',
                                              client_config=[
                                                  sub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='topic1'
                                                  ),
                                                  sub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='topic2'
                                                  )
                                              ]))

    g.add_edge('cloud1_broker1', 'cloud1', **edge_attrs(delay=2))
    g.add_edge('cloud1_client1', 'cloud1', **edge_attrs(delay=2))
    g.add_edge('cloud1_client2', 'cloud1', **edge_attrs(delay=2))
