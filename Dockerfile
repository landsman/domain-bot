FROM gradle:8.11.1-jdk21 AS builder

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY build.gradle.kts settings.gradle.kts $APP_HOME
COPY gradle $APP_HOME/gradle
COPY src $APP_HOME/src
COPY .git $APP_HOME

RUN touch .env
RUN GIT_SHA=$(git rev-parse --short HEAD) >> .env
RUN gradle bootJar --no-daemon --stacktrace -Dorg.gradle.warning.mode=all

FROM eclipse-temurin:21-jre-alpine

RUN apk add curl

ENV ARTIFACT_NAME=app.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY --from=builder /usr/app/build/libs/godaddy-bot-*.jar $ARTIFACT_NAME

EXPOSE $LISTEN_PORT

CMD ["java", "-jar", "app.jar"]
