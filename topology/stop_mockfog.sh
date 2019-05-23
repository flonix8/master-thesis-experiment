#!/bin/bash

. keys/set_access_keys.sh
aws --region eu-central-1 ec2 stop-instances --instance-ids i-0ffabf94678238ce0
