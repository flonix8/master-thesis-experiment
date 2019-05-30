#!/bin/bash

# Activate venv
. .venv/bin/activate

# Generate testbed config file
./generate_testbed_topology.py && \

# Run topology setup
ansible-playbook --tags bootstrap infrastructure_bootstrap.yml && \

# Run topology configuration
ansible-playbook -i ec2.py --key-file=keys/testbed.pem --ssh-common-args="-o StrictHostKeyChecking=no" infrastructure_config.yml

# Run experiment
#ansible-playbook -i ec2.py --key-file=keys/testbed.pem --ssh-common-args="-o StrictHostKeyChecking=no" experiment.yml

# Tear down setup
#ansible-playbook --tags destroy infrastructure.yml
