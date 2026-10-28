FROM amazoncorretto:27-alpine-jdk AS construccion
WORKDIR /construccion
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src src
RUN sh mvnw -B -DskipTests package

FROM amazoncorretto:27-alpine-jdk
WORKDIR /app
RUN addgroup -S nota && adduser -S -G nota nota
COPY --from=construccion /construccion/target/*.jar app.jar
USER nota
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s CMD wget -q -O /dev/null http://localhost:8080/actuator/health/liveness || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-Duser.timezone=UTC", "-jar", "/app/app.jar"]
