package ie.example.flag.evaluator.controller;

import module java.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.example.flag.evaluator.dto.Feature;
import ie.example.flag.evaluator.dto.Rule;
import ie.example.flag.evaluator.service.RulesService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlagController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagController.class);

    private final Environment environment;

    private final RulesService rulesService;

    @Autowired
    public FlagController(Environment environment,
                          RulesService rulesService) {
        this.environment = environment;
        this.rulesService = rulesService;
    }

    @GetMapping(value = "api/test")
    ResponseEntity testApiCalll() {
        LOGGER.info("Test yml value is: " + environment.getProperty("feature.new-checkout.enabled"));
        return ResponseEntity.ok().body(List.of("ES", "PT"));
    }

    @GetMapping(value = "api/eval")
    ResponseEntity<List<Rule>> evaluateFlags(@RequestParam(value = "feature") String feature,
                                  @RequestParam(value = "country") String country,
                                  @RequestParam(value = "appVersion") String appVersion,
                                  @RequestParam(value = "tier") String tier) {
        // TODO
        List<Rule> ruleMatch = new ArrayList<>();
        List<Rule> allRuleMatch = new ArrayList<>();
        List<Rule> anyRuleMatch = new ArrayList<>();

        Feature ft = rulesService.getFeaturesById().getOrDefault(feature, null);
        if (ft != null && ft.isEnabled()) {
            if (ft.getRuleGroups() != null) {
                ObjectMapper objectMapper = rulesService.getObjectMapper();
                Map<String, Map<String, Object>> featureAllRules = objectMapper
                        .convertValue(ft.getRuleGroups().getAll(), Map.class);
                allRuleMatch = rulesService.rulesMatcher(feature, country, appVersion, featureAllRules);

                Map<String, Map<String, Object>> featureAnyRules = objectMapper
                        .convertValue(ft.getRuleGroups().getAny(), Map.class);
                anyRuleMatch = rulesService.rulesMatcher(feature, country, appVersion, featureAnyRules);
            }
        }

        if (allRuleMatch.isEmpty() && anyRuleMatch.isEmpty()) {
            // no match
            return ResponseEntity.badRequest().body(Collections.emptyList());
        } else {
            // direct match(es)
            ruleMatch.addAll(allRuleMatch);
            ruleMatch.addAll(anyRuleMatch);
            return ResponseEntity.ok().body(ruleMatch);
        }
    }

    @GetMapping("/api/executetask")
    ResponseEntity execute() {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        ProcessInstantiationBuilder instance = engine.getRuntimeService().createProcessInstanceByKey("rules-init");
        instance.executeWithVariablesInReturn();
        return ResponseEntity.ok().body("Executed Camunda BPMN");
    }
}
