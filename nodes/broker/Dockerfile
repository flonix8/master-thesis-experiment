FROM ubuntu:bionic

RUN apt update && apt install -y \
        iperf \
        net-tools \
        iputils-ping \
        iproute2 \
        openjdk-8-jdk

COPY ./moquette /moquette-src
WORKDIR /moquette-src

RUN ./gradlew clean moquette-distribution:distMoquetteTar && \
    mkdir /moquette && \
    tar -xzvf distribution/build/*.tar.gz -C /moquette

CMD ["/moquette/bin/moquette.sh"]