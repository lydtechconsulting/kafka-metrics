package demo.kafka.service;

import java.util.UUID;

import demo.kafka.event.DemoEvent;
import demo.kafka.lib.KafkaClient;
import demo.kafka.mapper.JsonMapper;
import demo.kafka.rest.api.TriggerEventsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DemoService {

    @Autowired
    private final KafkaClient kafkaClient;

    /**
     * Processing happens asynchronously so the caller can return.
     *
     * Either sends a total number of events, or sends events for a set period of time.
     */
    @Async
    public void process(TriggerEventsRequest request) {
        int counter = 0;
        if(request.getNumberOfEvents() != null) {
            log.info("Sending {} events", request.getNumberOfEvents());
            for ( ; counter < request.getNumberOfEvents(); counter++) {
                sendEvent(request.getPayloadSizeBytes());
                if (counter % 100000 == 0) {
                    log.info("Total events sent: {}", counter);
                }
            }
        } else {
            log.info("Sending events for {} seconds", request.getPeriodToSendSeconds());
            long start = System.currentTimeMillis();
            long end = start + (request.getPeriodToSendSeconds() * 1000);
            while (System.currentTimeMillis() < end) {
                sendEvent(request.getPayloadSizeBytes());
                counter++;
                if (counter % 100000 == 0) {
                    log.info("Total events sent: " + counter);
                }
            }
        }
        log.info("Total events sent: {}", counter);
    }

    /**
     * Send an event asynchronously.
     */
    private void sendEvent(Integer payloadSizeBytes) {
        String key = UUID.randomUUID().toString();
        String payload = RandomStringUtils.randomAlphanumeric(payloadSizeBytes);

        DemoEvent demoEvent = DemoEvent.builder()
                .data(payload)
                .build();

        kafkaClient.sendMessageAsync(key, JsonMapper.writeToJson(demoEvent));
    }
}
