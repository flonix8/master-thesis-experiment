from networkx.classes import Graph

from common import node_attrs, edge_attrs, sub_config, pub_config


def reference_topology_bridge(g: Graph):
    # Cloud nodes
    g.add_node('cloud1', **node_attrs(type='zone'))
    g.add_node('cloud1_client1', **node_attrs(role='client',
                                              client_config=[
                                                  pub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='/push-infos/road-conditions',
                                                      frequency=5,
                                                      payload_size=10000,
                                                  ),
                                                  sub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='/car-telemetry/aggregated/#',
                                                  ),
                                                  sub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='/traffic-control/camera-feed/#',
                                                  ),
                                              ]))
    g.add_node('cloud1_broker1', **node_attrs(role='broker'))
    # Edge nodes
    g.add_node('edge1', **node_attrs(type='zone'))
    g.add_node('edge1_client1', **node_attrs(role='client',
                                             client_config=[
                                                 pub_config(
                                                     connect_to='edge1_broker1',
                                                     topic='/traffic-control/camera-feed/1',
                                                     frequency=20,
                                                     payload_size=500000,
                                                 ),
                                             ]))
    g.add_node('edge1_client2', **node_attrs(role='client',
                                             client_config=[
                                                 sub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='/car-telemetry/realtime/3',
                                                 ),
                                                 sub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='/car-telemetry/realtime/4',
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='/car-telemetry/aggregated/2',
                                                     frequency=5,
                                                     payload_size=4000,
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='/car-telemetry/realtime/2',
                                                     frequency=100,
                                                     payload_size=200,
                                                 ),
                                             ]))
    g.add_node('edge1_client3', **node_attrs(role='client',
                                             client_config=[
                                                 sub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/realtime/2',
                                                 ),
                                                 sub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/realtime/4',
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/aggregated/3',
                                                     frequency=5,
                                                     payload_size=4000,
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/realtime/3',
                                                     frequency=100,
                                                     payload_size=200,
                                                 ),
                                             ]))
    g.add_node('edge1_client4', **node_attrs(role='client',
                                             client_config=[
                                                 sub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/realtime/2',
                                                 ),
                                                 sub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/realtime/3',
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/aggregated/4',
                                                     frequency=5,
                                                     payload_size=4000,
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/realtime/4',
                                                     frequency=100,
                                                     payload_size=200,
                                                 ),
                                             ]))
    g.add_node('edge1_broker1', **node_attrs(role='broker'))
    g.add_node('edge1_broker2', **node_attrs(role='broker'))
    g.add_node('edge1_broker3', **node_attrs(role='broker'))

    # Connect cloud
    g.add_edge('cloud1_broker1', 'cloud1', **edge_attrs(delay=2))
    g.add_edge('cloud1_client1', 'cloud1', **edge_attrs(delay=2))
    # Connect edge
    g.add_edge('edge1_client1', 'edge1_broker1', **edge_attrs(delay=2))
    g.add_edge('edge1_client2', 'edge1_broker2', **edge_attrs(delay=2))
    g.add_edge('edge1_client3', 'edge1_broker3', **edge_attrs(delay=2))
    g.add_edge('edge1_client4', 'edge1_broker3', **edge_attrs(delay=2))
    g.add_edge('edge1_broker1', 'edge1_broker2', **edge_attrs(delay=2))
    g.add_edge('edge1_broker2', 'edge1_broker3', **edge_attrs(delay=2))
    g.add_edge('edge1', 'edge1_broker1', **edge_attrs())

    # Connect to cloud
    g.add_edge('edge1', 'cloud1', **edge_attrs(delay=20))


def reference_topology_delaygrouping(g: Graph):
    # Cloud nodes
    g.add_node('cloud1', **node_attrs(type='zone'))
    g.add_node('cloud1_client1', **node_attrs(role='client',
                                              client_config=[
                                                  pub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='/push-infos/road-conditions',
                                                      frequency=5,
                                                      payload_size=10000,
                                                  ),
                                                  sub_config(
                                                      connect_to='cloud1_broker1',
                                                      topic='/#',
                                                  ),
                                              ]))
    g.add_node('cloud1_broker1', **node_attrs(role='anchor', internal_ip='10.0.2.10'))
    # Edge zone #1
    g.add_node('edge1', **node_attrs(type='zone'))
    g.add_node('edge1_client1', **node_attrs(role='client',
                                             client_config=[
                                                 pub_config(
                                                     connect_to='edge1_broker1',
                                                     topic='/traffic-control/camera-feed/1',
                                                     frequency=20,
                                                     payload_size=500000,
                                                 ),
                                             ]))
    g.add_node('edge1_client2', **node_attrs(role='client',
                                             client_config=[
                                                 sub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='/car-telemetry/realtime/3',
                                                 ),
                                                 sub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='/car-telemetry/realtime/4',
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='/car-telemetry/aggregated/2',
                                                     frequency=5,
                                                     payload_size=4000,
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker2',
                                                     topic='/car-telemetry/realtime/2',
                                                     frequency=100,
                                                     payload_size=200,
                                                 ),
                                             ]))
    g.add_node('edge1_client3', **node_attrs(role='client',
                                             client_config=[
                                                 sub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/realtime/2',
                                                 ),
                                                 sub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/realtime/4',
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/aggregated/3',
                                                     frequency=5,
                                                     payload_size=4000,
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/realtime/3',
                                                     frequency=100,
                                                     payload_size=200,
                                                 ),
                                             ]))
    g.add_node('edge1_client4', **node_attrs(role='client',
                                             client_config=[
                                                 sub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/realtime/2',
                                                 ),
                                                 sub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/realtime/3',
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/aggregated/4',
                                                     frequency=5,
                                                     payload_size=4000,
                                                 ),
                                                 pub_config(
                                                     connect_to='edge1_broker3',
                                                     topic='/car-telemetry/realtime/4',
                                                     frequency=100,
                                                     payload_size=200,
                                                 ),
                                             ]))
    g.add_node('edge1_broker1', **node_attrs(role='broker', flavor='t3.micro'))
    g.add_node('edge1_broker2', **node_attrs(role='broker', flavor='t3.micro'))
    g.add_node('edge1_broker3', **node_attrs(role='broker', flavor='t3.micro'))
    # Edge zone #2
    g.add_node('edge2', **node_attrs(type='zone'))
    g.add_node('edge2_client1', **node_attrs(role='client',
                                             client_config=[
                                                 pub_config(
                                                     connect_to='edge2_broker1',
                                                     topic='/traffic-control/camera-feed/2',
                                                     frequency=20,
                                                     payload_size=500000,
                                                 ),
                                                 sub_config(
                                                     connect_to='edge2_broker1',
                                                     topic='/car-telemetry/realtime/5',
                                                 ),
                                             ]))
    g.add_node('edge2_client2', **node_attrs(role='client',
                                             client_config=[
                                                 pub_config(
                                                     connect_to='edge2_broker1',
                                                     topic='/car-telemetry/realtime/5',
                                                     frequency=100,
                                                     payload_size=200,
                                                 ),
                                             ]))
    g.add_node('edge2_broker1', **node_attrs(role='broker', flavor='t3.micro'))

    # Connect cloud
    g.add_edge('cloud1_broker1', 'cloud1', **edge_attrs(delay=2))
    g.add_edge('cloud1_client1', 'cloud1', **edge_attrs(delay=2))
    # Connect edge #1
    g.add_edge('edge1_client1', 'edge1_broker1', **edge_attrs(delay=2))
    g.add_edge('edge1_client2', 'edge1_broker2', **edge_attrs(delay=2))
    g.add_edge('edge1_client3', 'edge1_broker3', **edge_attrs(delay=2))
    g.add_edge('edge1_client4', 'edge1_broker3', **edge_attrs(delay=2))
    g.add_edge('edge1_broker1', 'edge1_broker2', **edge_attrs(delay=2))
    g.add_edge('edge1_broker2', 'edge1_broker3', **edge_attrs(delay=2))
    g.add_edge('edge1', 'edge1_broker1', **edge_attrs())
    # Connect edge #2
    g.add_edge('edge2_client1', 'edge2_broker1', **edge_attrs(delay=2))
    g.add_edge('edge2_client2', 'edge2_broker1', **edge_attrs(delay=2))
    g.add_edge('edge2', 'edge2_broker1', **edge_attrs())

    # Connect to cloud
    g.add_edge('edge1', 'cloud1', **edge_attrs(delay=20))
    g.add_edge('edge2', 'cloud1', **edge_attrs(delay=20))


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
