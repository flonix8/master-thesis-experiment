#!/bin/bash

. keys/set_access_keys.sh
aws --region eu-central-1 ec2 stop-instances --instance-ids i-0cb6866e4d9e0da37
