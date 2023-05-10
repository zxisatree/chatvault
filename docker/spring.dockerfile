FROM gradle:jdk17 AS BUILD
COPY . /home/gradle/kotlin_blog/
WORKDIR /home/gradle/kotlin_blog/
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:17-jre
ARG JAR_FILE=blog-0.0.1-SNAPSHOT.jar
COPY --from=BUILD /home/gradle/kotlin_blog/build/libs/${JAR_FILE} kotlin_blog.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "kotlin_blog.jar"]