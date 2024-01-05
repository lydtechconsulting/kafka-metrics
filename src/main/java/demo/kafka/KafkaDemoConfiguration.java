package demo.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.apache.kafka.common.security.scram.ScramLoginModule;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Slf4j
@ComponentScan(basePackages = {"demo.kafka"})
@Configuration
public class KafkaDemoConfiguration {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(final ConsumerFactory<String, String> consumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(final ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory(@Value("${kafka.bootstrap-servers}") final String bootstrapServers,
                                                           @Value("${kafka.consumer.maxPollIntervalMs}") final String maxPollIntervalMs,
                                                           @Value("${kafka.consumer.maxPollRecords}") final String maxPollRecords,
                                                           @Value("${kafka.confluent.monitoring.intercept.enabled}") final boolean interceptEnabled,
                                                           @Value("${kafka.sasl.enabled}") final Boolean saslEnabled,
                                                           @Value("${kafka.sasl.username}") final String username,
                                                           @Value("${kafka.sasl.password}") final String password) {
        final Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        if(interceptEnabled) {
            config.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, "io.confluent.monitoring.clients.interceptor.MonitoringConsumerInterceptor");
        }
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID());
        if(saslEnabled) {
            config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            config.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
            config.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(
                    "%s required username=\"%s\" password=\"%s\";", ScramLoginModule.class.getName(), username, password
            ));
        }
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, String> producerFactory(@Value("${kafka.bootstrap-servers}") final String bootstrapServers,
                                                           @Value("${kafka.producer.lingerMs}") final String lingerMs,
                                                           @Value("${kafka.producer.acks}") final String acks,
                                                           @Value("${kafka.confluent.monitoring.intercept.enabled}") final boolean interceptEnabled,
                                                           @Value("${kafka.sasl.enabled}") final Boolean saslEnabled,
                                                           @Value("${kafka.sasl.username}") final String username,
                                                           @Value("${kafka.sasl.password}") final String password) {
        final Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        if(interceptEnabled) {
            config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor");
        }
        config.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        config.put(ProducerConfig.ACKS_CONFIG, acks);
        if(saslEnabled) {
            config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            config.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
            config.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(
                    "%s required username=\"%s\" password=\"%s\";", ScramLoginModule.class.getName(), username, password
            ));
        }
        return new DefaultKafkaProducerFactory<>(config);
    }
}
