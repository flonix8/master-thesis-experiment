#!/bin/bash

# Build moquette (benefit from cached gradle dependencies)
./gradlew clean moquette-distribution:distMoquetteTar && \
mkdir -p /moquette && \
tar -xzvf distribution/build/*.tar.gz -C /moquette