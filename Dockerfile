FROM java:8-jdk-alpine

COPY ./target/aggregate-period-0.0.1-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch aggregate-period-0.0.1-SNAPSHOT.jar'

ENTRYPOINT ["java","-jar","aggregate-period-0.0.1-SNAPSHOT.jar"]