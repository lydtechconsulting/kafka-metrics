package demo.kafka.lib;

import java.util.concurrent.Future;

import demo.kafka.properties.KafkaDemoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaClient {
    @Autowired
    private final KafkaDemoProperties properties;

    @Autowired
    private final KafkaTemplate kafkaTemplate;

    public Future<RecordMetadata> sendMessageAsync(String key, String payload) {
        try {
            final ProducerRecord<String, String> record = new ProducerRecord<>(properties.getOutboundTopic(), key, payload);
            final Future<RecordMetadata> result = kafkaTemplate.send(record);
            return result;
        } catch (Exception e) {
            String message = "Error sending message to topic " + properties.getOutboundTopic();
            log.error(message);
            throw new RuntimeException(message, e);
        }
    }
}
