package demo.kafka.util;

import demo.kafka.event.DemoEvent;
import demo.kafka.rest.api.TriggerEventsRequest;

public class TestData {

    public static String INBOUND_DATA = "event data";

    public static DemoEvent buildDemoEvent(String id) {
        return DemoEvent.builder()
                .data(INBOUND_DATA)
                .build();
    }

    public static TriggerEventsRequest buildTriggerEventsRequest() {
        return TriggerEventsRequest.builder()
                .numberOfEvents(10)
                .payloadSizeBytes(100)
                .build();
    }
}
