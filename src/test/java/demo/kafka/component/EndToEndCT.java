package demo.kafka.component;

import demo.kafka.rest.api.TriggerEventsRequest;
import dev.lydtech.component.framework.client.service.ServiceClient;
import dev.lydtech.component.framework.extension.TestContainersSetupExtension;
import dev.lydtech.component.framework.mapper.JsonMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Slf4j
@ExtendWith(TestContainersSetupExtension.class)
public class EndToEndCT {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = ServiceClient.getInstance().getBaseUrl();
    }

    /**
     * Send in a REST request to trigger sending and consuming multiple events.
     *
     * Configure either NUMBER_OF_EVENTS or PERIOD_TO_SEND_SECONDS, and PAYLOAD_SIZE_BYTES.
     *
     * Monitor the broker, topics and messages, and view the broker, consumer and producer metrics in Control Center.
     */
    @Test
    public void testFlow() throws Exception {

        Long NUMBER_OF_EVENTS = 100000000l;
//        Long PERIOD_TO_SEND_SECONDS = 600l;
        Long PAYLOAD_SIZE_BYTES = 1000l;

        TriggerEventsRequest request = TriggerEventsRequest.builder()
                .numberOfEvents(NUMBER_OF_EVENTS)
//                .periodToSendSeconds(PERIOD_TO_SEND_SECONDS)
                .payloadSizeBytes(PAYLOAD_SIZE_BYTES)
                .build();

        RequestSpecification requestSpec = new RequestSpecBuilder()
                .setBaseUri(ServiceClient.getInstance().getBaseUrl())
                .build();
        RestAssured.given()
                .spec(requestSpec)
                .contentType("application/json")
                .body(JsonMapper.writeToJson(request))
                .post("/v1/demo/trigger")
                .then()
                .statusCode(202);
    }
}
