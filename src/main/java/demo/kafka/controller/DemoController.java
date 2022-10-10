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
        try {
            demoService.process(request);
            return ResponseEntity.accepted().build();
        } catch(Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
