FROM ubuntu:bionic

# Prepare system with instrumentation for containernet
RUN apt-get update && apt-get install -y \
    net-tools \
    iputils-ping \
    iproute2 

# Client node software
RUN apt update && apt install -y \
    python3.7 \
    python3-pip && \
    ln -s /usr/bin/python3.7 /usr/bin/python

ADD . /client/
WORKDIR /client

# Install dependencies
RUN python -m pip install --upgrade pip && \
    python -m pip install -r requirements.txt

CMD ["/bin/bash"]
