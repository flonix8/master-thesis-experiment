# Description
This is the companion repository to my master's thesis. It contains some orchestration and infrastructure code in order to run the evaluation experiment that I described in my thesis. \
Its goal is to set up a cloud testbed on AWS EC2 with delay-wise topology simulation and then deploy a network of MQTT brokers onto some of the nodes. The rest of the nodes are provisioned with MQTT benchmark clients simulating publishing and subscribing clients, publishing a configurable workload. \

# Acknowledgement
The main work (not part of this repository, but needed for correct operation) is based on the MQTT Broker "moquette", which is a Java implementation of a MQTTv3.1.1-compatible broker: https://github.com/moquette-io/moquette \
Some Ansible scripts for testbed orchestration are based on the MockFog-IaC project: https://github.com/OpenFogStack/MockFog-IaC

# Quickstart
## Preparation
* Clone the customized moquette build into `nodes/broker/moquette`:
  ```bash
  git clone git@github.com:flonix8/moquette.git ./nodes/broker/moquette
  ```

* Make sure that your active Python (3.6+) environment fulfills the dependencies from `topology/requirements.txt`. Preferably create a venv inside `topology`.

* Rename `topology/testbed_files/testbed_config_example.yml` to `topology/testbed_files/testbed_config.yml` and fill in config values:
  * Enter your AWS credentials
  * Set the key name property to the name of a valid keypair in your EC2 account (you must have access to the file later)
  * Set a base image for the testbed nodes. You can use `ami-0031cb806c358cba4` for now (Note that this image is only available in eu-central-1, so you should keep the region if you intend to use it)

* Make sure that your AWS credentials are set in your environment, i.e., AWS CLI works.

* Make sure that you have a working Ansible installation (2.8+) on your PATH

## Create testbed and experiment configuration
* cd into `./topology`
* Adapt testbed topology and workload config if needed (see `generate_testbed_topology.py` and `topologies.py` in `testbed_files/python/`)
* Generate testbed config file:
  ```bash
  testbed_files/python/generate_testbed_topology.py
  ```
## Setup testbed
* Run topology bootstrap:
  ```bash
  ansible-playbook infrastructure_bootstrap.yml --tags bootstrap
  ```
* Run topology configuration:
  ```bash
  ansible-playbook -i inventory/ec2.py --key-file=path/to/ssh/keyfile.pem --ssh-common-args="-o StrictHostKeyChecking=no" infrastructure_config.yml 
  ```
## Run experiment
You can now run an experiment of your choice. Both provided experiment files `experiment_*.yml` build on the same set of tags to provide access to orchestration commands (and must be executed in the following order, unless you know what you are doing): `setup`, `prepare`, `run`, `shutdown`, `collect` /

* Run experiment:
  ```bash
  ansible-playbook -i inventory/ec2.py --key-file=path/to/ssh/keyfile.pem --ssh-common-args="-o StrictHostKeyChecking=no" experiment_xy.yml --tags experiment_step_tag
  ```
