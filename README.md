# Kafka Spring Boot Metrics Demo Project

Spring Boot application demonstrating Kafka monitoring and JMX metrics utilising Conduktor Platform and Confluent Control Center.

This repo accompanies the following series of articles on Kafka Monitoring and Metrics:

- [Kafka Monitoring & Metrics: With Conduktor](https://www.lydtechconsulting.com/blog-kafka-metrics-conduktor.html): monitoring a Kafka cluster using Conduktor to provide a view on the key broker and topic metrics.
- [Kafka Monitoring & Metrics: With Confluent Control Center](https://www.lydtechconsulting.com/blog-kafka-metrics-control-center.html): monitoring a Kafka cluster using Confluent Control Center to provide a view on the key broker, topic, consumer and producer metrics.

## Overview

The application provides a REST endpoint that accepts a request to trigger sending events.  Either the number of events to produce, or the period of time over which to produce events, can be specified.  The application consumes the events from the topic.  The consumer and the producer have interceptors to publish JMX metrics, so these can be observed in the Conflent Control Center, along with metrics on the Kafka broker. The Kafka broker, topics, and events can also be monitored in Conduktor Platform or the Confluent Control Center. 

## Conduktor Platform

Conduktor Platform provides a UI over the Kafka cluster, providing a view of the configuration, data and information on the brokers, topics and messages.  It also shows the JMX metrics exported from the broker.

In order to display Kafka broker metrics Conduktor requires the Kafka broker to be started with the `jmx_prometheus_javaagent` agent.  Note this is only applicable when running Conduktor via `docker-compose`, as it is not supported when running via the component tests.

From the root dir download the jar via:
```
curl https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.17.2/jmx_prometheus_javaagent-0.17.2.jar -o ./jmx_prometheus_javaagent-0.17.2.jar
```

Navigate to the Conduktor Platform once it is running, having followed either the `Run Spring Boot Application` steps or the `Component Testing` steps below:
```
http://localhost:8080
```

Login with the default credentials `admin@conduktor.io` / `admin`.

See more on Conduktor Platform at `https://www.conduktor.io/`

## Confluent Control Center

Confluent Control Center provides a UI over the Kafka cluster, providing a view of the configuration, data and information on the brokers, topics and messages.  It also shows the JMX metrics exported from the broker, consumers and producers.

Navigate to the Control Center once it is running, having followed either the `Run Spring Boot Application` steps or the `Component Testing` steps below:
```
http://localhost:9021
```

See more on Confluent Control Center at `https://docs.confluent.io/platform/current/control-center/index.html`

## Run Spring Boot Application

### Build
```
mvn clean install
```

### Run Docker Containers

From the root dir run one of the `docker-compose` files to start dockerised Kafka, Zookeeper, and either Conduktor Platform or Confluent Control Center:

- a single broker Kafka cluster with Conduktor Platform:
```
docker-compose -f docker-compose-conduktor-single.yml up -d
```

- a multiple broker Kafka cluster (3 broker nodes, with replication of 3 and min in-sync replicas of 3) with Conduktor Platform:
```
docker-compose -f docker-compose-conduktor-multiple.yml up -d
```

- a single broker Kafka cluster with Confluent Control Center:
```
docker-compose -f docker-compose-control-center-single.yml up -d
```

- a multiple broker Kafka cluster (3 broker nodes, with replication of 3 and min in-sync replicas of 3) with Confluent Control Center:
```
docker-compose -f docker-compose-control-center-multiple.yml up -d
```

### Start Demo Spring Boot Application
```
java -jar target/kafka-metrics-demo-1.0.0.jar
```

### Trigger Events

To trigger sending and consuming a configurable number of events send one of the following requests to the application using curl.

To trigger sending a specified number of events:
```
curl -v -d '{"numberOfEvents":100000, "payloadSizeBytes":200}' -H "Content-Type: application/json" -X POST http://localhost:9001/v1/demo/trigger
```

To trigger sending events for a specified period of time:
```
curl -v -d '{"periodToSendSeconds":600, "payloadSizeBytes":200}' -H "Content-Type: application/json" -X POST http://localhost:9001/v1/demo/trigger
```

In both cases the size in bytes of the event payloads to create must be specified.

The request should be accepted with a `202 ACCEPTED`, as the event sending processing is asynchronous. 

The broker, producer and consumer metrics can then be viewed in the Confluent Control Center, or the Conduktor Platform.

### Configuring The Broker And Application

A number of parameters can be configured in the `src/main/resources/application.yml` (e.g. `producer` `lingerMs`, `acks` and `async`) and the `docker-compose` `yml` (e.g. `KAFKA_NUM_PARTITIONS`, `KAFKA_DEFAULT_REPLICATION_FACTOR` and `KAFKA_MIN_INSYNC_REPLICAS`) to view their impact on the system.

`producer` `async` configures whether the producer produces asynchronously (fire and forget) or waits synchronously for the result of each send.

## Component Tests

### Overview

The tests call the dockerised demo application over REST to trigger sending and consuming a configurable number of events.  The broker, producer and consumer data, configuration and metrics can then be viewed in Conduktor Platform or the Confluent Control Center. 

To enable Control Center, ensure the following are configured in the `pom.xml`:

```
<kafka.control.center.enabled>true</kafka.control.center.enabled>
<kafka.control.center.export.metrics.enabled>true</kafka.control.center.export.metrics.enabled>
<conduktor.enabled>false</conduktor.enabled>
```

To enable Conduktor, ensure the following are configured in the `pom.xml`:

```
<kafka.control.center.enabled>false</kafka.control.center.enabled>
<kafka.control.center.export.metrics.enabled>false</kafka.control.center.export.metrics.enabled>
<conduktor.enabled>true</conduktor.enabled>
```

For more on the component tests see: https://github.com/lydtechconsulting/component-test-framework

### Test

The component test used to trigger sending events is `demo.kafka.component.EndToEndCT`.  Edit the request to determine the quantity of events to send.

Send a specified number of events: 
```
TriggerEventsRequest request = TriggerEventsRequest.builder()
    .numberOfEvents(NUMBER_OF_EVENTS)
    .payloadSizeBytes(PAYLOAD_SIZE_BYTES)
    .build();
```

Send events for a specified period of time:
```
TriggerEventsRequest request = TriggerEventsRequest.builder()
    .periodToSendSeconds(PERIOD_TO_SEND_SECONDS)
    .payloadSizeBytes(PAYLOAD_SIZE_BYTES)
    .build();
```

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

Run tests (by default the containers are left running after the test run so Conduktor or Control Center metrics can be viewed):
```
mvn test -Pcomponent
```

Run tests tearing down the containers at the end:
```
mvn test -Pcomponent -Dcontainers.stayup=false
```

### Configuring The Broker And Application

A number of parameters can be configured for the component test run in the `src/test/resources/application-component-test.yml` (e.g. `producer` `lingerMs`, `acks` and `async`) and the `pom.xml` `maven-surefire-plugin` `systemPropertyVariables` (e.g. `kafka.topic.partition.count`, `kafka.topic.replication.factor`, `kafka.min.insync.replicas`) to view their impact on the system.  Note that for these settings to be applied it will require the containers to be restarted if they have been left up.

## Docker Clean Up

Manual clean up (if left containers up):
```
docker rm -f $(docker ps -aq)
```

Further docker clean up (if network issues and to remove old networks/volumes):
```
docker network prune

docker system prune

docker volume prune
```
