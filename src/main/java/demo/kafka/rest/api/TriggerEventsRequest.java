package demo.kafka.rest.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriggerEventsRequest {

    private Integer numberOfEvents;
    private Integer periodToSendSeconds;
    private Integer payloadSizeBytes;
}
