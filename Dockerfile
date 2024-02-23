FROM bellsoft/liberica-openjdk-debian:17
RUN addgroup spring-boot-group && adduser --ingroup spring-boot-group spring-boot
USER spring-boot:spring-boot-group
VOLUME /tmp
ARG JAR_FILE=TaskManager-0.0.1-SNAPSHOT.jar
WORKDIR /application
COPY target/${JAR_FILE} application.jar
COPY target/dependency lib
ENTRYPOINT exec java ${JAVA_OPTS} -cp lib/*:application.jar com.alkl1m.taskmanager.TaskManagerApplication