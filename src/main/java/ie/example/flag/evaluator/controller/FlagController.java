package ie.example.flag.evaluator.controller;

import module java.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.example.flag.evaluator.domain.ApiResponse;
import ie.example.flag.evaluator.domain.TestApiResponse;
import ie.example.flag.evaluator.dto.Feature;
import ie.example.flag.evaluator.dto.Rule;
import ie.example.flag.evaluator.enums.Country;
import ie.example.flag.evaluator.service.RulesService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlagController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagController.class);

    public static final String FEATURE_NEW_CHECKOUT_ENABLED = "feature.new-checkout.enabled";

    private final Environment environment;

    private final RulesService rulesService;

    private final ObjectMapper objectMapper;

    @Autowired
    public FlagController(Environment environment,
                          RulesService rulesService,
                          ObjectMapper objectMapper) {
        this.environment = environment;
        this.rulesService = rulesService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "api/test")
    ResponseEntity<TestApiResponse> testApiCall() {
        LOGGER.info("Test yml value is: {}", environment.getProperty(FEATURE_NEW_CHECKOUT_ENABLED));
        return ResponseEntity.ok().body(new TestApiResponse(Arrays.stream(Country.values()).map(Enum::name).toList()));
    }

    @GetMapping(value = "/api/executetask", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> executeTaskToLoadInitialRuleSet() {
        // TODO move to scheduled call or job,
        //  Note: This GET method/trigger is for testing purposes only to force load of initial rule set
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        ProcessInstantiationBuilder instance = engine.getRuntimeService().createProcessInstanceByKey("rules-init");
        instance.executeWithVariablesInReturn();
        return ResponseEntity.ok().body("Executed Camunda BPMN");
    }

    @GetMapping(value = "api/eval", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> evaluateFlags(@RequestParam(value = "feature") String feature,
                                              @RequestParam(value = "country") String country,
                                              @RequestParam(value = "appVersion") String appVersion,
                                              @RequestParam(value = "tier") String tier) {
        Feature ft = rulesService.getFeaturesById().getOrDefault(feature, null);
        LOGGER.info("Evaluating feature: {} for country: {} appVersion: {} tier: {}", feature, country, appVersion, tier);
        if (ft != null && ft.enabled()) {
            if (ft.ruleGroups() != null) {
                List<Rule> allRuleMatch = getAllRuleMatch(country, appVersion, tier, ft);
                List<Rule> anyRuleMatch = getAnyRuleMatch(country, appVersion, tier, ft);
                return createResponseFromMatches(feature, allRuleMatch, anyRuleMatch);
            }
        }
        LOGGER.warn("Feature {} is not enabled.", feature);
        return ResponseEntity.badRequest().body(new ApiResponse(Collections.emptyList()));
    }

    private ResponseEntity<ApiResponse> createResponseFromMatches(String feature,
                                                                  List<Rule> allRuleMatch,
                                                                  List<Rule> anyRuleMatch) {
        if (allRuleMatch.isEmpty() && anyRuleMatch.isEmpty()) {
            // no match
            LOGGER.warn("Rules did not match for feature {}", feature);
            return ResponseEntity.badRequest().body(new ApiResponse(Collections.emptyList()));
        } else {
            // direct match(es)
            List<Rule> ruleMatch = new ArrayList<>();
            ruleMatch.addAll(allRuleMatch);
            ruleMatch.addAll(anyRuleMatch);
            LOGGER.info("Rules set for feature {} is {}", feature, ruleMatch);
            return ResponseEntity.ok().body(new ApiResponse(ruleMatch));
        }
    }

    private List<Rule> getAnyRuleMatch(String country, String appVersion, String tier, Feature ft) {
        List<Rule> anyRuleMatch;
        Map<String, Map<String, Object>> featureAnyRules = objectMapper
                .convertValue(ft.ruleGroups().any(), Map.class);
        anyRuleMatch = rulesService.rulesMatcher(country,
                appVersion, tier, featureAnyRules);
        return anyRuleMatch;
    }

    private List<Rule> getAllRuleMatch(String country, String appVersion, String tier, Feature ft) {
        List<Rule> allRuleMatch;
        Map<String, Map<String, Object>> featureAllRules = objectMapper
                .convertValue(ft.ruleGroups().all(), Map.class);
        allRuleMatch = rulesService.rulesMatcher(country,
                appVersion, tier, featureAllRules);
        return allRuleMatch;
    }
}
