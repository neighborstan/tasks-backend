FROM gitlab.dellin.ru:5005/docker/origin/gradle:7.6.0-jdk19-alpine AS builder
COPY --chown=gradle:gradle . ./
RUN gradle :api:bootJar :watcher:bootJar -g /home/gradle/cash --no-daemon

FROM gitlab.dellin.ru:5005/docker/origin/openjdk:19-alpine
RUN mkdir -p /app && apk update && apk add yq postgresql-client gettext
WORKDIR /app
COPY --from=builder /home/gradle/api/build/libs/api.jar /app/api.jar
COPY --from=builder /home/gradle/watcher/build/libs/watcher.jar /app/watcher.jar
ENTRYPOINT sh