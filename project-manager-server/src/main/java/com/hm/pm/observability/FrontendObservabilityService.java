package com.hm.pm.observability;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Service
public class FrontendObservabilityService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public FrontendObservabilityService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public FrontendEvent create(CreateFrontendEvent command) {
        validate(command);

        String payloadJson = toPayloadJson(command.payload());
        Instant createdAt = Instant.now();

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            INSERT INTO frontend_observability_event(event_type, level, occurred_at, payload_json, created_at)
                            VALUES (?, ?, ?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, command.eventType());
            statement.setString(2, command.level().name());
            statement.setTimestamp(3, Timestamp.from(command.occurredAt()));
            statement.setString(4, payloadJson);
            statement.setTimestamp(5, Timestamp.from(createdAt));
            return statement;
        }, keyHolder);

        Number id = keyHolder.getKey();
        if (id == null) {
            throw new IllegalStateException("failed to create frontend observability event");
        }

        return new FrontendEvent(
                id.longValue(),
                command.eventType(),
                command.level(),
                command.occurredAt(),
                command.payload()
        );
    }

    private void validate(CreateFrontendEvent command) {
        if (command.eventType() == null || command.eventType().isBlank()) {
            throw new IllegalArgumentException("eventType is required");
        }
        if (command.level() == null) {
            throw new IllegalArgumentException("level is required");
        }
        if (command.occurredAt() == null) {
            throw new IllegalArgumentException("occurredAt is required");
        }
        if (command.payload() == null) {
            throw new IllegalArgumentException("payload is required");
        }
    }

    private String toPayloadJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("payload cannot be serialized", ex);
        }
    }

    public record CreateFrontendEvent(String eventType,
                                      EventLevel level,
                                      Instant occurredAt,
                                      Map<String, Object> payload) {
    }

    public record FrontendEvent(Long id,
                                String eventType,
                                EventLevel level,
                                Instant occurredAt,
                                Map<String, Object> payload) {
    }

    public enum EventLevel {
        INFO,
        WARN,
        ERROR
    }
}
