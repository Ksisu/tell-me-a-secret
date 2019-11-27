FROM hseeberger/scala-sbt:8u222_1.3.4_2.13.1 as builder
WORKDIR /app
COPY build.sbt /app/build.sbt
COPY project /app/project
RUN sbt update
COPY . .
RUN sbt stage && \
    chmod -R u=rX,g=rX /app/target/universal/stage && \
    chmod u+x,g+x /app/target/universal/stage/bin/tell-me-a-secret

FROM openjdk:8-alpine
USER root
RUN apk add --no-cache bash && \
    adduser -S -u 1001 tell-me-a-secret
USER 1001
EXPOSE 8080
ENTRYPOINT ["/app/bin/tell-me-a-secret"]
CMD []
COPY --from=builder --chown=1001:root /app/target/universal/stage /app
