#!/bin/bash

USER=ec2-user
NODE=$1

ssh -i keys/mockfog.pem $USER@$NODE
