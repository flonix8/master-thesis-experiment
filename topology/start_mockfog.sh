#!/bin/bash
INSTANCE_ID=i-0ffabf94678238ce0
. keys/set_access_keys.sh
aws --region eu-central-1 ec2 start-instances --instance-ids $INSTANCE_ID
aws --region eu-central-1 ec2 describe-instances --instance-ids $INSTANCE_ID | jq ".Reservations[0].Instances[0].PublicIpAddress"
