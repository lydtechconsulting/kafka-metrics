package demo.kafka.component;

import demo.kafka.rest.api.TriggerEventsRequest;
import dev.lydtech.component.framework.client.service.ServiceClient;
import dev.lydtech.component.framework.extension.TestContainersSetupExtension;
import dev.lydtech.component.framework.mapper.JsonMapper;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Slf4j
@ExtendWith(TestContainersSetupExtension.class)
public class EndToEndCT {

    /**
     * Send in a REST request to trigger sending and consuming multiple events.
     *
     * The REST call returns immediately, as it sends the events asychronously.  The test will therefore complete quickly
     * while events may still be sent and consumed by the application.
     *
     * Configure either NUMBER_OF_EVENTS or PERIOD_TO_SEND_SECONDS, and PAYLOAD_SIZE_BYTES.
     *
     * Configure the application-component-test.yml, for example to define whether the Producer send should be synchronous or asynchronous.
     *
     * Monitor the broker, topics and messages, and view the broker, consumer and producer metrics in Control Center.
     */
    @Test
    public void testFlow() {

        Integer NUMBER_OF_EVENTS = 100000;
        Integer PERIOD_TO_SEND_SECONDS = 600;
        Integer PAYLOAD_SIZE_BYTES = 200;

        TriggerEventsRequest request = TriggerEventsRequest.builder()
//                .numberOfEvents(NUMBER_OF_EVENTS)
                .periodToSendSeconds(PERIOD_TO_SEND_SECONDS)
                .payloadSizeBytes(PAYLOAD_SIZE_BYTES)
                .build();

        RestAssured.given()
                .spec(ServiceClient.getInstance().getRequestSpecification())
                .contentType("application/json")
                .body(JsonMapper.writeToJson(request))
                .post("/v1/demo/trigger")
                .then()
                .statusCode(202);
    }
}
