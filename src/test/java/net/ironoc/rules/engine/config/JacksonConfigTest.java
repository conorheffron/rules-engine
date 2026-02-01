package net.ironoc.rules.engine.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class JacksonConfigTest {

    @Test
    void objectMapper_disablesFailOnUnknownProperties() {
        JacksonConfig config = new JacksonConfig();

        ObjectMapper mapper = config.objectMapper();

        assertFalse(
                mapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES),
                "FAIL_ON_UNKNOWN_PROPERTIES should be disabled"
        );
    }
}
