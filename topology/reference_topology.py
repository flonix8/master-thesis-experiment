from mininet.net import Containernet
from mininet.log import info, setLogLevel
from mininet.cli import CLI

setLogLevel('info')

net = Containernet(controller=None)

info('*** Adding broker containers ***\n')
be1 = net.addDocker('be1', dimage='broker-node')
be2 = net.addDocker('be2', dimage='broker-node')
bc1 = net.addDocker('bc1', dimage='broker-node')

info('*** Adding broker links ***\n')
net.addLink(be1, be2, params1={'ip': '10.0.0.1/24'}, params2={'ip': '10.0.0.2/24'})
net.addLink(be1, bc1, params1={'ip': '10.0.1.1/24'}, params2={'ip': '10.0.1.2/24'})
net.addLink(be2, bc1, params1={'ip': '10.0.2.1/24'}, params2={'ip': '10.0.2.2/24'})

info('*** Adding client containers ***\n')
ce1 = net.addDocker('ce1', dimage='client-node')
ce2 = net.addDocker('ce2', dimage='client-node')
cc1 = net.addDocker('cc1', dimage='client-node')

info('*** Connecting clients to brokers ***\n')
net.addLink(ce1, be1, params1={'ip': '10.0.3.1/24'}, params2={'ip': '10.0.3.2/24'})
net.addLink(ce2, be2, params1={'ip': '10.0.4.1/24'}, params2={'ip': '10.0.4.2/24'})
net.addLink(cc1, bc1, params1={'ip': '10.0.5.1/24'}, params2={'ip': '10.0.5.2/24'})

info('*** Running CLI... ***\n')
CLI(net)

info('*** Stopping network... ***\n')
net.stop()
