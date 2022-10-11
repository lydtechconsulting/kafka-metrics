package demo.kafka.controller;

import demo.kafka.rest.api.TriggerEventsRequest;
import demo.kafka.service.DemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/demo")
public class DemoController {

    @Autowired
    private final DemoService demoService;

    @PostMapping("/trigger")
    public ResponseEntity<Void> trigger(@RequestBody TriggerEventsRequest request) {
        if(request.getPayloadSizeBytes() == null || request.getPayloadSizeBytes()<1) {
            log.error("Invalid payload size bytes");
            return ResponseEntity.badRequest().build();
        }
        if(request.getNumberOfEvents() != null && request.getPeriodToSendSeconds() != null) {
            log.error("Both number of events to send and period to send are set");
            return ResponseEntity.badRequest().build();
        }
        if(request.getNumberOfEvents() == null && request.getPeriodToSendSeconds() == null) {
            log.error("Neither number of events to send or period to send are set");
            return ResponseEntity.badRequest().build();
        }
        if(request.getNumberOfEvents() != null && request.getNumberOfEvents()<1) {
            log.error("Invalid number of events");
            return ResponseEntity.badRequest().build();
        }

        if(request.getPeriodToSendSeconds() != null && request.getPeriodToSendSeconds()<1) {
            log.error("Invalid period to send");
            return ResponseEntity.badRequest().build();
        }

        try {
            demoService.process(request);
            return ResponseEntity.accepted().build();
        } catch(Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
