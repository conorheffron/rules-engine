package net.ironoc.rules.engine.controller;

import module java.base;

import net.ironoc.rules.engine.domain.ApiResponse;
import net.ironoc.rules.engine.domain.TestApiResponse;
import net.ironoc.rules.engine.dto.Feature;
import net.ironoc.rules.engine.dto.Rule;
import net.ironoc.rules.engine.enums.Country;
import net.ironoc.rules.engine.enums.RuleGroup;
import net.ironoc.rules.engine.service.DetailCacheI;
import net.ironoc.rules.engine.service.RuleServiceI;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlagController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagController.class);

    private final RuleServiceI rulesService;

    private final RuntimeService runtimeService;

    private final DetailCacheI featureDetailsService;

    @Autowired
    public FlagController(RuleServiceI rulesService,
                          RuntimeService runtimeService,
                          DetailCacheI featureDetailsService) {
        this.rulesService = rulesService;
        this.runtimeService = runtimeService;
        this.featureDetailsService = featureDetailsService;
    }

    @GetMapping(value = "api/test")
    ResponseEntity<TestApiResponse> testApiCall() {
        return ResponseEntity.ok().body(new TestApiResponse(Arrays.stream(Country.values()).map(Enum::name).toList()));
    }

    @GetMapping(value = "/api/executetask", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> executeTaskToLoadInitialRuleSet() {
        // TODO move to scheduled call or job,
        //  Note: This GET method/trigger is for testing purposes only to force load of initial rule set
        ProcessInstanceWithVariables result = runtimeService
                .createProcessInstanceByKey("Rules_matcher")
                .executeWithVariablesInReturn();

        String pid = result.getProcessInstanceId();
        LOGGER.info("Started Rules_matcher process instance id={}", pid);

        return ResponseEntity.ok().body("{\"processInstanceId\":\"" + pid + "\"}");
    }

    @GetMapping(value = "api/eval", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse> evaluateFlags(@RequestParam(value = "feature") String feature,
                                              @RequestParam(value = "country") String country,
                                              @RequestParam(value = "appVersion") String appVersion,
                                              @RequestParam(value = "tier") String tier) {
        Feature ft = featureDetailsService.getFeaturesById().getOrDefault(feature, null);
        LOGGER.info("Evaluating feature: {} for country: {} appVersion: {} tier: {}", feature, country, appVersion, tier);
        if (ft != null && ft.enabled()) {
            if (ft.ruleGroups() != null) {
                List<Rule> allRuleMatch = rulesService.getRuleMatchByRuleGroup(country, appVersion,
                        tier, ft, RuleGroup.ALL);
                List<Rule> anyRuleMatch = rulesService.getRuleMatchByRuleGroup(country, appVersion,
                        tier, ft, RuleGroup.ANY);
                return rulesService.createResponseFromMatches(feature, allRuleMatch, anyRuleMatch);
            }
        }
        LOGGER.warn("Feature {} is not enabled.", feature);
        return ResponseEntity.badRequest().body(new ApiResponse(Collections.emptyList()));
    }
}
