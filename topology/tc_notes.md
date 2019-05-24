Add HTB qdisc to root on eth1 (in order to be able to classify traffic per destination ip and control bandwidth per destination):

```bash
    tc qdisc add dev eth1 root handle 1: htb
```

Add one class / corresponding filter and netem qdisc for every destination ip:

```bash
    tc class add dev eth1 parent 1: classid 1:1 htb rate 100mbps # Careful, this is megaBYTE/s!
    tc filter add dev eth1 protocol ip parent 1:0 u32 match ip dst 10.0.2.101 classid 1:1
    tc qdisc add dev eth1 parent 1:1 handle 2: netem delay 50000
```