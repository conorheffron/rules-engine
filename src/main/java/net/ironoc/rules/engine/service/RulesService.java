package net.ironoc.rules.engine.service;

import module java.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ironoc.rules.engine.domain.ApiResponse;
import net.ironoc.rules.engine.dto.Feature;
import net.ironoc.rules.engine.dto.Rule;
import net.ironoc.rules.engine.enums.FeatureFlag;
import net.ironoc.rules.engine.enums.RuleGroup;
import net.ironoc.rules.engine.enums.RuleOperator;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RulesService implements RuleServiceI {

    private static final Logger LOGGER = LoggerFactory.getLogger(RulesService.class);

    private final Environment environment;

    private final ObjectMapper objectMapper;

    private final DetailCacheI featureDetailsService;

    @Autowired
    public RulesService(Environment environment,
                        ObjectMapper objectMapper,
                        DetailCacheI featureDetailsService) {
        this.environment = environment;
        this.objectMapper = objectMapper;
        this.featureDetailsService = featureDetailsService;
    }



    @Override
    public void execute(DelegateExecution execution) {
        LOGGER.info("Initialize rules from local features config with vars: {}", execution.getVariables());
        Map<String, Object> appSettingConfig = Binder.get(environment)
                .bind("feature", Map.class)
                .orElseThrow(() -> new IllegalArgumentException("No properties found with prefix 'feature'"));

        for (Map.Entry<String, Object> entry : appSettingConfig.entrySet()) {
            String featureKey = entry.getKey();
            Feature feature = objectMapper.convertValue(entry.getValue(), Feature.class);
            // store feature
            featureDetailsService.put(featureKey, feature);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> createResponseFromMatches(String feature,
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

    @Override
    public List<Rule> getRuleMatchByRuleGroup(String country, String appVersion, String tier,
                                              Feature ft, RuleGroup ruleGroup) {
        Map<String, Map<String, Object>> ruleGrps = switch (ruleGroup) {
            case ALL -> ft.ruleGroups().all();
            case ANY -> ft.ruleGroups().any();
        };
        Map<String, Map<String, Object>> featureRules = objectMapper
                .convertValue(ruleGrps, Map.class);
        return rulesMatcher(country,  appVersion, tier, featureRules);
    }

    protected List<Rule> rulesMatcher(String country, String appVersion, String tier,
                                   Map<String, Map<String, Object>> featureAllRules) {
        List<Rule> ruleMatch = new ArrayList<>();
        if (featureAllRules != null && !featureAllRules.isEmpty()) {
            for (Map<String, Object> ruleMap : featureAllRules.values()) {
                Rule rule = this.objectMapper.convertValue(ruleMap, Rule.class);
                RuleOperator ruleOperator = RuleOperator.fromStr(rule.op());
                // validate features (check for matching rules)
                validateStringMatch(country, rule, ruleOperator, ruleMatch, FeatureFlag.COUNTRY);
                validateAppVersionMatch(appVersion, rule, ruleOperator, ruleMatch);
                validateStringMatch(tier, rule, ruleOperator, ruleMatch, FeatureFlag.TIER);
            }
        }
        return ruleMatch;
    }

    private void validateStringMatch(String inputStr, Rule rule, RuleOperator ruleOperator, List<Rule> ruleMatch,
                                     FeatureFlag featureType) {
        if (rule.attr().equalsIgnoreCase(featureType.name())) {
            switch (ruleOperator) {
                case RuleOperator.IN:
                case RuleOperator.EQ:
                    if (rule.values().containsValue(inputStr)) {
                        ruleMatch.add(rule);// append Rule
                    }
                    break;
                default:
                    LOGGER.warn("OP not supported: {}", rule.op());
                    break;
            }
        }
    }

    private void validateAppVersionMatch(String appVersion, Rule rule, RuleOperator ruleOperator, List<Rule> ruleMatch) {
        if (rule.attr().equalsIgnoreCase(FeatureFlag.APPVERSION.name())) {
            switch (ruleOperator) {
                case RuleOperator.GTE:
                case RuleOperator.GT:
                    Optional<String> appVersionOptional = rule.values().values().stream().findFirst();
                    int vers = Integer.parseInt(appVersionOptional.orElse("-1"));
                    if (Integer.parseInt(appVersion) >= vers) {
                        ruleMatch.add(rule);// append Rule
                    }
                    break;
                default:
                    LOGGER.warn("Version OP not supported: {}", rule.op());
                    break;
            }
        }
    }
}
