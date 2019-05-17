#!/bin/bash
ansible-playbook -i ec2.py --key-file=keys/mockfog.pem --ssh-common-args="-o StrictHostKeyChecking=no" main.yml
