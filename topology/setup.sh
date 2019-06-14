#!/bin/bash

# Activate venv (needed for inventory script)
. .venv/bin/activate

# Run topology setup
ansible-playbook infrastructure_bootstrap.yml --tags bootstrap && \

# Run topology configuration
ansible-playbook -i inventory/ec2.py --key-file=keys/testbed.pem --ssh-common-args="-o StrictHostKeyChecking=no" infrastructure_config.yml 