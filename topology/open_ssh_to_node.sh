#!/bin/bash

USER=ec2-user
NODE=$1

ssh -i keys/testbed.pem $USER@$NODE
