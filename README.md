# Kafka Spring Boot Metrics Demo Project

Spring Boot application demonstrating Kafka monitoring and JMX metrics.

## Overview

The application has a REST endpoint that accepts a request to trigger sending events.  Either the number of events to produce, or the period of time over which to produce events, can be specified.  The application consumes the events from the topic.  The consumer and the producer have interceptors to publish JMX metrics, so these can be observed in the Conflent Control Center, along with metrics on the Kafka broker. The Kafka broker, topics, and events can also be monitored in the Control Center. 

## Build
```
mvn clean install
```

## Kafka Confluent Control Center

Confluent Control Center is a UI over the Kafka cluster, providing a view of the configuration, data and information on the brokers, topics and messages.  It also shows the JMX metrics exported from the broker, consumers and producers.

Navigate to the Control Center once it is running, having followed either the `Run Spring Boot Application` steps or the `Component Testing` steps below:
```
http://localhost:9021
```

## Run Spring Boot Application

### Run docker containers

From root dir run the following to start dockerised Kafka, Zookeeper, and Confluent Control Center:
```
docker-compose up -d
```

### Start demo spring boot application
```
java -jar target/kafka-metrics-demo-1.0.0.jar
```

### Trigger events

To trigger sending and consuming a configurable number of events send one of the following requests to the application using curl.

To trigger sending a specified number of events :
```
curl -v -d '{"numberOfEvents":100000, "payloadSizeBytes":100}' -H "Content-Type: application/json" -X POST http://localhost:9001/v1/demo/trigger
```

To trigger sending events for a specified period of time:
```
curl -v -d '{"periodToSendSeconds":300, "payloadSizeBytes":100}' -H "Content-Type: application/json" -X POST http://localhost:9001/v1/demo/trigger
```

In both cases the size in bytes of the event payloads to create must be specified.

The request should be accepted with a 202 ACCEPTED, as the event sending processing is asynchronous. 

The broker, producer and consumer metrics can then be viewed in the Confluent Control Center.

### Configuring the broker and application

A number of parameters can be configured in the `src/main/resources/application.yml` (e.g. `producer lingerMs`) and the `docker-compose.yml` (e.g. `KAFKA_NUM_PARTITIONS`) files to view their impact on the system. 

## Component Tests

### Overview

The tests call the dockerise demo application over REST to trigger sending and consuming a configurable number of events.  The broker, producer and consumer metrics can then be viewed in the Confluent Control Center.

For more on the component tests see: https://github.com/lydtechconsulting/component-test-framework

### Build

Build Spring Boot application jar:
```
mvn clean install
```

Build Docker container:
```
docker build -t ct/kafka-metrics-demo:latest .
```

### Test Execution

Run tests (by default the containers are left running after the test run so Control Center metrics can be viewed):
```
mvn test -Pcomponent
```

Run tests tearing down the containers at the end:
```
mvn test -Pcomponent -Dcontainers.stayup=false
```

### Configuring the broker and application

A number of parameters can be configured for the component test run in the `src/test/resources/application-component-test.yml` (e.g. `producer lingerMs`) and the `pom.xml` `maven-surefire-plugin` `systemPropertyVariables` (e.g. `kafka.topic.partition.count`) to view their impact on the system.  Note that for these settings to be applied it will require the containers to be restarted.

## Docker Clean Up

Manual clean up (if left containers up):
```
docker rm -f $(docker ps -aq)
```

Further docker clean up (if network issues and to remove old images/volumes):
```
docker network prune

docker system prune

docker volume prune
```
