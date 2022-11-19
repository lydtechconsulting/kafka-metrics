package demo.kafka.service;

import java.util.concurrent.Future;

import demo.kafka.lib.KafkaClient;
import demo.kafka.properties.KafkaDemoProperties;
import demo.kafka.rest.api.TriggerEventsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DemoServiceTest {

    private KafkaClient mockKafkaClient;
    private KafkaDemoProperties mockKafkaDemoProperties;
    private DemoService service;

    @BeforeEach
    public void setUp() {
        mockKafkaClient = mock(KafkaClient.class);
        mockKafkaDemoProperties = mock(KafkaDemoProperties.class);
        service = new DemoService(mockKafkaClient, mockKafkaDemoProperties);
    }

    /**
     * Ensure the Kafka client is called to emit the expected number of events asynchronously.
     */
    @Test
    public void testProcess_NumberOfEvents() {
        when(mockKafkaDemoProperties.isKafkaProducerAsync()).thenReturn(true);

        TriggerEventsRequest testEvent = TriggerEventsRequest.builder()
                .numberOfEvents(10)
                .payloadSizeBytes(100)
                .build();
        service.process(testEvent);
        verify(mockKafkaClient, times(testEvent.getNumberOfEvents().intValue())).sendMessageAsync(anyString(), anyString());
    }

    /**
     * Ensure the Kafka client is called to emit events asynchronously over a period of time.
     */
    @Test
    public void testProcess_PeriodToSend() {
        when(mockKafkaDemoProperties.isKafkaProducerAsync()).thenReturn(true);

        TriggerEventsRequest testEvent = TriggerEventsRequest.builder()
                .periodToSendSeconds(1)
                .payloadSizeBytes(100)
                .build();
        service.process(testEvent);
        verify(mockKafkaClient, atLeast(10)).sendMessageAsync(anyString(), anyString());
    }

    @Test
    public void testProcess_SynchronousSend() {
        when(mockKafkaDemoProperties.isKafkaProducerAsync()).thenReturn(false);
        when(mockKafkaClient.sendMessageAsync(anyString(), anyString())).thenReturn(mock(Future.class));

        TriggerEventsRequest testEvent = TriggerEventsRequest.builder()
                .periodToSendSeconds(1)
                .payloadSizeBytes(100)
                .build();
        service.process(testEvent);
        verify(mockKafkaClient, atLeast(10)).sendMessageAsync(anyString(), anyString());
    }
}
