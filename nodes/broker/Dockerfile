FROM alpine:3.9

RUN apk add openjdk8-jre-base

COPY --from=moquette-build /moquette /moquette

ENTRYPOINT ["/moquette/bin/moquette.sh"]