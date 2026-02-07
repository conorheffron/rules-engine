package net.ironoc.rules.engine.service;

import module java.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ironoc.rules.engine.dto.Rule;
import net.ironoc.rules.engine.enums.FeatureFlag;
import net.ironoc.rules.engine.enums.RuleOperator;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class RulesServiceTest {

    private static RulesService newService() {
        Environment env = mock(Environment.class);
        ObjectMapper objectMapper = new ObjectMapper();
        FeatureDetailService featureDetailService = new FeatureDetailService();
        return new RulesService(env, objectMapper, featureDetailService);
    }

    @Test
    void rulesMatcher_countryIn_matches() {
        RulesService service = newService();

        Map<String, Map<String, Object>> rules = Map.of(
                "r1", Map.of(
                        "attr", "COUNTRY",
                        "op", "IN",
                        "values", Map.of("0", "ES", "1", "PT")
                )
        );

        List<Rule> matches = service.rulesMatcher("ES", "10", "FREE", rules);

        assertEquals(1, matches.size());
        assertEquals(FeatureFlag.COUNTRY.name(), matches.getFirst().attr());
        assertEquals(RuleOperator.IN.name(), matches.getFirst().op());
    }

    @Test
    void rulesMatcher_appVersionGte_matches() {
        RulesService service = newService();

        Map<String, Map<String, Object>> rules = Map.of(
                "r1", Map.of(
                        "attr", "APPVERSION",
                        "op", "GTE",
                        "values", Map.of("0", "10")
                )
        );

        List<Rule> matches = service.rulesMatcher("ES", "12", "FREE", rules);

        assertEquals(1, matches.size());
        assertEquals(FeatureFlag.APPVERSION.name(), matches.getFirst().attr());
        assertEquals(RuleOperator.GTE.name(), matches.getFirst().op());
    }

    @Test
    void rulesMatcher_countryWithUnsupportedOperator_doesNotMatch() {
        RulesService service = newService();

        Map<String, Map<String, Object>> rules = Map.of(
                "r1", Map.of(
                        "attr", "COUNTRY",
                        "op", "GTE",          // not supported for COUNTRY in validateCountryMatch
                        "values", Map.of("0", "ES")
                )
        );

        List<Rule> matches = service.rulesMatcher("ES", "12", "FREE", rules);

        assertTrue(matches.isEmpty());
    }
}
