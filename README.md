# Kafka Spring Boot Metrics Demo Project

Spring Boot application demonstrating Kafka monitoring and JMX metrics.

## Build
```
mvn clean install
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

### Kafka Confluent Control Center

Confluent Control Center is a UI over the Kafka cluster, providing a view of the configuration, data and information on the brokers, topics and messages.

Navigate to the Control Center:
```
http://localhost:9021
```

### Command Line Tools

#### View topics

Jump on to Kafka docker container:
```
docker exec -ti kafka bash
```

List topics:
```
kafka-topics --list --bootstrap-server localhost:9092
```

## Component Tests

The tests call the dockerise demo application over REST to trigger sending and consuming a configurable number of events.  The broker, producer and consumer metrics can then be viewed in the Confluent Control Center.

For more on the component tests see: https://github.com/lydtechconsulting/component-test-framework

Build Spring Boot application jar:
```
mvn clean install
```

Build Docker container:
```
docker build -t ct/kafka-metrics-demo:latest .
```

Run tests (by default the containers are left running after the test run so Control Center metrics can be viewed):
```
mvn test -Pcomponent
```

Run tests tearing down the containers at the end:
```
mvn test -Pcomponent -Dcontainers.stayup=false
```

Manual clean up (if left containers up):
```
docker rm -f $(docker ps -aq)
```

Further docker clean up if network issues:
```
docker network prune
```
