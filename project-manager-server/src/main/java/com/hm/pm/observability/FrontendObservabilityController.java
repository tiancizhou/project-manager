package com.hm.pm.observability;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/observability")
public class FrontendObservabilityController {

    private final FrontendObservabilityService frontendObservabilityService;

    public FrontendObservabilityController(FrontendObservabilityService frontendObservabilityService) {
        this.frontendObservabilityService = frontendObservabilityService;
    }

    @PostMapping("/frontend-events")
    public ResponseEntity<FrontendObservabilityService.FrontendEvent> create(
            @RequestBody CreateFrontendObservabilityEventRequest request
    ) {
        try {
            FrontendObservabilityService.FrontendEvent event = frontendObservabilityService.create(
                    new FrontendObservabilityService.CreateFrontendEvent(
                            request.eventType(),
                            request.level(),
                            request.occurredAt(),
                            request.payload()
                    )
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    public record CreateFrontendObservabilityEventRequest(String eventType,
                                                          FrontendObservabilityService.EventLevel level,
                                                          Instant occurredAt,
                                                          Map<String, Object> payload) {
    }
}
