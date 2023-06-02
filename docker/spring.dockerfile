FROM gradle:jdk17 as CACHE
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY build.gradle.kts /home/gradle/cache_code/
WORKDIR /home/gradle/cache_code
RUN gradle clean build -x bootJar --no-daemon

FROM gradle:jdk17 AS BUILD
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /home/gradle/chatvault/
WORKDIR /home/gradle/chatvault/
RUN gradle assemble -x test --no-daemon

FROM eclipse-temurin:17-jre
ARG JAR_FILE=chatvault-0.0.1-SNAPSHOT.jar
COPY --from=BUILD /home/gradle/chatvault/build/libs/${JAR_FILE} chatvault.jar
COPY env.properties ./
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "chatvault.jar"]