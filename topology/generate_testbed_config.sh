#!/bin/bash

# Activate venv
. .venv/bin/activate

# Generate testbed config file
testbed_files/python/generate_testbed_topology.py && \