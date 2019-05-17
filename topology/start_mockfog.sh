#!/bin/bash
INSTANCE_ID=i-04d21e47e7df34e62
. keys/set_access_keys.sh
aws --region eu-west-1 ec2 start-instances --instance-ids $INSTANCE_ID
aws ec2 describe-instances --instance-ids $INSTANCE_ID | jq ".Reservations[0].Instances[0].PublicIpAddress"
