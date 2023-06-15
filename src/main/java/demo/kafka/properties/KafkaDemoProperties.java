package demo.kafka.properties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties("kafka")
@Getter
@Setter
@Validated
public class KafkaDemoProperties {
    @NotNull private String outboundTopic;

    @NotNull
    @Valid
    private Producer producer;

    @Getter
    @Setter
    @Validated
    public static class Producer {

        /**
         * Whether to send messages asynchronously.
         */
        @NotNull
        private boolean async;
    }
}
