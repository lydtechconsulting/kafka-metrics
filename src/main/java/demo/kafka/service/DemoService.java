package demo.kafka.service;

import java.util.UUID;

import demo.kafka.event.DemoEvent;
import demo.kafka.lib.KafkaClient;
import demo.kafka.mapper.JsonMapper;
import demo.kafka.rest.api.TriggerEventsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     */
    @Async
    public void process(TriggerEventsRequest request) {
        for (int i=0; i<request.getNumberOfEvents(); i++) {
            String key = UUID.randomUUID().toString();
            String payload = UUID.randomUUID().toString();

            DemoEvent demoEvent = DemoEvent.builder()
                    .id(UUID.randomUUID().toString())
                    .data(payload)
                    .build();

            kafkaClient.sendMessageAsync(key, JsonMapper.writeToJson(demoEvent));
            if(i % 10000 == 0){
                log.info("Total events sent: {}", i);
            }
        }
    }
}
