FROM openjdk:latest
#ARG JAR_FILE=HttpNotification.main.jar
ARG JAR_FILE=https://github.com/chain-action/http-notification/releases/download/v1.0.3/HttpNotification.main.jar
ADD ${JAR_FILE} /app/app.jar
COPY config.yml /app/config.yml
#RUN /usr/bin/java --version
RUN mkdir -p /app
WORKDIR /app
#CMD /usr/bin/java -Dfile.encoding=UTF-8 -jar app.jar --createtable
#ENTRYPOINT /usr/bin/java -Dfile.encoding=UTF-8 -jar app.jar
ENTRYPOINT if test ! -f "initialized.flag"; then /usr/bin/java -Dfile.encoding=UTF-8 -jar app.jar --createtable; touch initialized.flag; fi && /usr/bin/java -Dfile.encoding=UTF-8 -jar app.jar
EXPOSE 8010
