FROM hseeberger/scala-sbt:8u181_2.12.8_1.2.8 as builder
COPY build.sbt /app/build.sbt
COPY project /app/project
WORKDIR /app
RUN sbt update test:update it:update
COPY . .
RUN sbt compile stage


FROM openjdk:8
WORKDIR /app
COPY --from=builder /app/target/universal/stage /app
USER root
RUN useradd --system --create-home --uid 1001 --gid 0 tell-me-a-secret && \
    chmod -R u=rX,g=rX /app && \
    chmod u+x,g+x /app/bin/tell-me-a-secret && \
    chown -R 1001:root /app
USER 1001

EXPOSE 8080
ENTRYPOINT ["/app/bin/tell-me-a-secret"]
CMD []
