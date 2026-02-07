package net.ironoc.rules.engine.controller;

import module java.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ironoc.rules.engine.domain.ApiResponse;
import net.ironoc.rules.engine.dto.Feature;
import net.ironoc.rules.engine.dto.RuleGroups;
import net.ironoc.rules.engine.enums.FeatureFlag;
import net.ironoc.rules.engine.service.FeatureDetailService;
import net.ironoc.rules.engine.service.RulesService;
import org.camunda.bpm.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class FlagControllerTest {

    private static ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    private static FeatureDetailService featureDetailService = new FeatureDetailService();

    private static RulesService rulesService() {
        Environment env = mock(Environment.class);
        return new RulesService(env, objectMapper(), featureDetailService);
    }

    private static FlagController controllerWith(RulesService service) {
        RuntimeService runtimeService = mock(RuntimeService.class);
        return new FlagController(service, runtimeService, featureDetailService);
    }

    @Test
    void evaluateFlags_featureNotFound_returnsBadRequestAndEmptyList() {
        // given
        RulesService service = rulesService(); // empty features map
        FlagController controller = controllerWith(service);

        // when
        ResponseEntity<ApiResponse> response =
                controller.evaluateFlags("missing-feature", "ES", "12", "FREE");

        // then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().rules().isEmpty());
    }

    @Test
    void evaluateFlags_featureDisabled_returnsBadRequestAndEmptyList() {
        // given
        featureDetailService.clear();
        RulesService service = rulesService();
        featureDetailService.put("new-checkout", new Feature(false, null));
        FlagController controller = controllerWith(service);

        // when
        ResponseEntity<ApiResponse> response =
                controller.evaluateFlags("new-checkout", "ES", "12", "FREE");

        // then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().rules().isEmpty());
    }

    @Test
    void evaluateFlags_matchesFromAllAndAny_returnsOkAndCombinedList() {
        // given
        RulesService service = rulesService();

        Map<String, Map<String, Object>> all = Map.of(
                "r1", Map.of(
                        "attr", "COUNTRY",
                        "op", "IN",
                        "values", Map.of("0", "ES")
                )
        );

        Map<String, Map<String, Object>> any = Map.of(
                "r2", Map.of(
                        "attr", "TIER",
                        "op", "EQ",
                        "values", Map.of("0", "FREE")
                )
        );

        Feature feature = new Feature(true, new RuleGroups(all, any));
        featureDetailService.clear();
        featureDetailService.put("new-checkout", feature);

        FlagController controller = controllerWith(service);

        // when
        ResponseEntity<ApiResponse> response =
                controller.evaluateFlags("new-checkout", "ES", "12", "FREE");


        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().rules().size());

        assertTrue(response.getBody().rules().stream().anyMatch(r -> FeatureType.COUNTRY.name().equalsIgnoreCase(r.attr())));
        assertTrue(response.getBody().rules().stream().anyMatch(r -> FeatureType.TIER.name().equalsIgnoreCase(r.attr())));
    }
}
