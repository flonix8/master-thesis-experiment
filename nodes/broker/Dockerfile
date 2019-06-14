#FROM alpine:3.9

#RUN apk add openjdk8-jre-base
FROM openjdk:8-jre-slim

COPY --from=moquette-build /moquette /moquette

ENTRYPOINT ["/moquette/bin/moquette.sh"]