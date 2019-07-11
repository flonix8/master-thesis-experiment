#FROM alpine:3.9

FROM openjdk:11-jre-slim

RUN apt update && apt install -y iputils-ping

COPY --from=moquette-build /moquette /moquette

ENTRYPOINT ["/moquette/bin/moquette.sh"]