FROM ubuntu:bionic

RUN apt-get update && apt-get install -y tcpdump 

ENTRYPOINT [ "tcpdump" ]
CMD ["-i eth0"]