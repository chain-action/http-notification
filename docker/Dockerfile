FROM openjdk:latest
#ARG JAR_FILE=HttpNotification.main.jar
ARG JAR_FILE=https://github.com/chain-action/http-notification/releases/download/v1.0.2/HttpNotification.main.jar
#COPY ${JAR_FILE} /app/app.jar
ADD ${JAR_FILE} /app/app.jar
COPY config.yml /app/config.yml
#RUN /usr/bin/java --version
RUN mkdir -p /app
WORKDIR /app
CMD /usr/bin/java -Dfile.encoding=UTF-8 -jar app.jar --createtable
ENTRYPOINT /usr/bin/java -Dfile.encoding=UTF-8 -jar app.jar
EXPOSE 8010
