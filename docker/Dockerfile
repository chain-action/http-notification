FROM openjdk:latest
#ARG JAR_FILE=HttpNotification.main.jar
#COPY ${JAR_FILE} app.jar
ADD http://gitlab.xeon24.local:8980/counters/http-notification/uploads/f15f7f0abd58beb5061d01613c32d47b/HttpNotification.main.jar /app/app.jar
COPY config.yml /app/config.yml
#RUN /usr/bin/java --version
RUN mkdir -p /app
WORKDIR /app
CMD /usr/bin/java -Dfile.encoding=UTF-8 -jar app.jar --createtable
#CMD /usr/bin/java -Dfile.encoding=UTF-8 -jar app.jar
ENTRYPOINT /usr/bin/java -Dfile.encoding=UTF-8 -jar app.jar
EXPOSE 8010
#COPY http://gitlab.xeon24.local:8980/counters/http-notification/uploads/f15f7f0abd58beb5061d01613c32d47b/HttpNotification.main.jar app.jar
#RUN apt-get update && apt-get install -qq -y --no-install-recommends wget && rm -rf /var/lib/apt/lists/*
#ENTRYPOINT ["java","-Dfile.encoding=UTF-8","-jar","app.jar"]
#ENTRYPOINT ["java","-Dfile.encoding=UTF-8","-jar","app.jar","-c"]
