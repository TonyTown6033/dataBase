#!/bin/sh
set -e

./mvnw -q -DskipTests package
SPRING_PROFILES_ACTIVE=prod java -jar target/dataBase-0.0.1-SNAPSHOT.jar
