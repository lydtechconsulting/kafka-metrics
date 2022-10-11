package demo.kafka.service;

import demo.kafka.lib.KafkaClient;
import demo.kafka.rest.api.TriggerEventsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DemoServiceTest {

    private KafkaClient mockKafkaClient;
    private DemoService service;

    @BeforeEach
    public void setUp() {
        mockKafkaClient = mock(KafkaClient.class);
        service = new DemoService(mockKafkaClient);
    }

    /**
     * Ensure the Kafka client is called to emit the expected number of events.
     */
    @Test
    public void testProcess_NumberOfEvents() {
        TriggerEventsRequest testEvent = TriggerEventsRequest.builder()
                .numberOfEvents(10)
                .payloadSizeBytes(100)
                .build();
        service.process(testEvent);
        verify(mockKafkaClient, times(testEvent.getNumberOfEvents().intValue())).sendMessageAsync(anyString(), anyString());
    }

    /**
     * Ensure the Kafka client is called to emit events over a period of time.
     */
    @Test
    public void testProcess_PeriodToSend() {
        TriggerEventsRequest testEvent = TriggerEventsRequest.builder()
                .periodToSendSeconds(1)
                .payloadSizeBytes(100)
                .build();
        service.process(testEvent);
        verify(mockKafkaClient, atLeast(10)).sendMessageAsync(anyString(), anyString());
    }
}
