package demo.kafka.service;

import demo.kafka.lib.KafkaClient;
import demo.kafka.rest.api.TriggerEventsRequest;
import demo.kafka.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
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
    public void testProcess() {
        TriggerEventsRequest testEvent = TestData.buildTriggerEventsRequest();

        service.process(testEvent);

        verify(mockKafkaClient, times(testEvent.getNumberOfEvents().intValue())).sendMessageAsync(anyString(), anyString());
    }
}
