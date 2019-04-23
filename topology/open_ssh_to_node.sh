#!/bin/bash

USER=ec2-user
NODE=$1

ssh -i mockfog.pem $USER@$NODE
