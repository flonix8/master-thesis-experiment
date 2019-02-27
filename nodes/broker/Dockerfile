FROM ubuntu:trusty

    
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0xB1998361219BD9C9 && \
    echo 'deb http://repos.azulsystems.com/ubuntu stable main' > /etc/apt/sources.list.d/zulu.list && \
    apt update && \
    apt install -y \
        iperf \
        zulu-8

COPY ./moquette /moquette-src
WORKDIR /moquette-src

RUN ./gradlew clean moquette-distribution:distMoquetteTar && \
    mkdir /moquette && \
    tar -xzvf distribution/build/*.tar.gz -C /moquette

CMD ["/moquette/bin/moquette.sh"]