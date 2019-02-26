from mininet.net import Containernet
from mininet.log import info, setLogLevel
from mininet.cli import CLI

setLogLevel('info')

net = Containernet(controller=None)

info('*** Adding broker containers ***\n')
be1 = net.addDocker('be1', dimage='ubuntu:trusty')
be2 = net.addDocker('be2', dimage='ubuntu:trusty')
bc1 = net.addDocker('bc1', dimage='ubuntu:trusty')

info('*** Adding broker links ***\n')
net.addLink(be1, be2)
net.addLink(be1, bc1)
net.addLink(be2, bc1)

info('*** Adding client containers ***\n')
ce1 = net.addDocker('ce1', dimage='ubuntu:trusty')
ce2 = net.addDocker('ce2', dimage='ubuntu:trusty')
cc1 = net.addDocker('cc1', dimage='ubuntu:trusty')

info('*** Connecting clients to brokers ***\n')
net.addLink(ce1, be1)
net.addLink(ce2, be2)
net.addLink(cc1, bc1)

info('*** Running CLI... ***\n')
CLI(net)

info('*** Stopping network... ***\n')
net.stop()
