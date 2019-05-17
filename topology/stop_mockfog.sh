#!/bin/bash

. keys/set_access_keys.sh
aws --region eu-west-1 ec2 stop-instances --instance-ids i-04d21e47e7df34e62
