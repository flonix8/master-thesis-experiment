from mininet.net import Containernet
from mininet.log import info, setLogLevel
from mininet.cli import CLI
from time import sleep

worker_containers = {}
benchmark_count = 0

setLogLevel('info')

net = Containernet(controller=None)

def create_worker_containers(count):
    info('*** Creating {} worker containers ***\n'.format(count))
    for i in range(count):
        worker_containers['w{}'.format(i+1)] = net.addDocker('w{}'.format(i+1), dimage='severalnines/sysbench', volumes=['output:/output/'])

def run_benchmark(test_type='cpu', cpu_quota=1, threads=1):
    global benchmark_count
    info('*** Running benchmark: type={}, quota={}, threads={} ***\n'.format(test_type, cpu_quota, threads))
    for worker_id in worker_containers:
        outfile_path = '/output/{count}_{worker_id}_{quota}c_t{threads}'.format(count=str(benchmark_count).zfill(3), worker_id=worker_id, quota=cpu_quota, threads=threads)
        if cpu_quota is not None:
            worker_containers[worker_id].update_resources(cpu_period=100000, cpu_quota=int(cpu_quota*100000))
        worker_containers[worker_id].sendCmd('sysbench --threads=1 {type} run > {outfile}\n'.format(type=test_type, outfile=outfile_path))
    
    # Wait for all containers to finish benchmarking
    for worker_id in worker_containers:
        worker_containers[worker_id].waitOutput()

    benchmark_count += 1


create_worker_containers(2)

net.start()

### Run benchmarks

run_benchmark(cpu_quota=0.1, threads=1)
run_benchmark(cpu_quota=0.1, threads=2)
run_benchmark(cpu_quota=0.1, threads=3)
run_benchmark(cpu_quota=0.1, threads=4)

# run_benchmark(cpu_quota=1)
# run_benchmark(cpu_quota=0.9)
# run_benchmark(cpu_quota=0.8)
# run_benchmark(cpu_quota=0.7)
# run_benchmark(cpu_quota=0.6)
# run_benchmark(cpu_quota=0.5)
# run_benchmark(cpu_quota=0.4)
# run_benchmark(cpu_quota=0.3)
# run_benchmark(cpu_quota=0.2)
# run_benchmark(cpu_quota=0.1)

# run_benchmark(test_type='fileio', cpu_quota=1)
# run_benchmark(test_type='fileio', cpu_quota=0.9)
# run_benchmark(test_type='fileio', cpu_quota=0.8)
# run_benchmark(test_type='fileio', cpu_quota=0.7)
# run_benchmark(test_type='fileio', cpu_quota=0.6)
# run_benchmark(test_type='fileio', cpu_quota=0.5)
# run_benchmark(test_type='fileio', cpu_quota=0.4)
# run_benchmark(test_type='fileio', cpu_quota=0.3)
# run_benchmark(test_type='fileio', cpu_quota=0.2)
# run_benchmark(test_type='fileio', cpu_quota=0.1)

# run_benchmark(test_type='memory', cpu_quota=1)
# run_benchmark(test_type='memory', cpu_quota=0.9)
# run_benchmark(test_type='memory', cpu_quota=0.8)
# run_benchmark(test_type='memory', cpu_quota=0.7)
# run_benchmark(test_type='memory', cpu_quota=0.6)
# run_benchmark(test_type='memory', cpu_quota=0.5)
# run_benchmark(test_type='memory', cpu_quota=0.4)
# run_benchmark(test_type='memory', cpu_quota=0.3)
# run_benchmark(test_type='memory', cpu_quota=0.2)
# run_benchmark(test_type='memory', cpu_quota=0.1)

# run_benchmark(threads=2, cpu_quota=1)
# run_benchmark(threads=2, cpu_quota=0.9)
# run_benchmark(threads=2, cpu_quota=0.8)
# run_benchmark(threads=2, cpu_quota=0.7)
# run_benchmark(threads=2, cpu_quota=0.6)
# run_benchmark(threads=2, cpu_quota=0.5)
# run_benchmark(threads=2, cpu_quota=0.4)
# run_benchmark(threads=2, cpu_quota=0.3)
# run_benchmark(threads=2, cpu_quota=0.2)
# run_benchmark(threads=2, cpu_quota=0.1)

# run_benchmark(threads=2, test_type='fileio', cpu_quota=1)
# run_benchmark(threads=2, test_type='fileio', cpu_quota=0.9)
# run_benchmark(threads=2, test_type='fileio', cpu_quota=0.8)
# run_benchmark(threads=2, test_type='fileio', cpu_quota=0.7)
# run_benchmark(threads=2, test_type='fileio', cpu_quota=0.6)
# run_benchmark(threads=2, test_type='fileio', cpu_quota=0.5)
# run_benchmark(threads=2, test_type='fileio', cpu_quota=0.4)
# run_benchmark(threads=2, test_type='fileio', cpu_quota=0.3)
# run_benchmark(threads=2, test_type='fileio', cpu_quota=0.2)
# run_benchmark(threads=2, test_type='fileio', cpu_quota=0.1)

# run_benchmark(threads=2, test_type='memory', cpu_quota=1)
# run_benchmark(threads=2, test_type='memory', cpu_quota=0.9)
# run_benchmark(threads=2, test_type='memory', cpu_quota=0.8)
# run_benchmark(threads=2, test_type='memory', cpu_quota=0.7)
# run_benchmark(threads=2, test_type='memory', cpu_quota=0.6)
# run_benchmark(threads=2, test_type='memory', cpu_quota=0.5)
# run_benchmark(threads=2, test_type='memory', cpu_quota=0.4)
# run_benchmark(threads=2, test_type='memory', cpu_quota=0.3)
# run_benchmark(threads=2, test_type='memory', cpu_quota=0.2)
# run_benchmark(threads=2, test_type='memory', cpu_quota=0.1)

info('*** Done benchmarking. ***\n')

info('*** Stopping network... ***\n')
net.stop()
