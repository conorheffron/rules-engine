package net.ironoc.rules.engine.service;

import module java.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ironoc.rules.engine.dto.Feature;
import net.ironoc.rules.engine.dto.Rule;
import net.ironoc.rules.engine.enums.FeatureType;
import net.ironoc.rules.engine.enums.RuleOperator;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class RulesService implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(RulesService.class);

    private final Environment environment;

    private final Map<String, Feature> featuresById = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    @Autowired
    public RulesService(Environment environment, ObjectMapper objectMapper) {
        this.environment = environment;
        this.objectMapper = objectMapper;
    }

    public Map<String, Feature> getFeaturesById() {
        return featuresById;
    }

    @Override
    public void execute(DelegateExecution execution) {
        LOGGER.info("Initialize rules from local features config");
        Map<String, Object> appSettingConfig = Binder.get(environment)
                .bind("feature", Map.class)
                .orElseThrow(() -> new IllegalArgumentException("No properties found with prefix 'feature'"));

        for (Map.Entry<String, Object> entry : appSettingConfig.entrySet()) {
            String featureKey = entry.getKey();
            Feature feature = objectMapper.convertValue(entry.getValue(), Feature.class);
            // store feature
            featuresById.put(featureKey, feature);
        }
    }

    public List<Rule> rulesMatcher(String country, String appVersion, String tier,
                                   Map<String, Map<String, Object>> featureAllRules) {
        List<Rule> ruleMatch = new ArrayList<>();
        if (featureAllRules != null && !featureAllRules.isEmpty()) {
            for (Map<String, Object> ruleMap : featureAllRules.values()) {
                Rule rule = this.objectMapper.convertValue(ruleMap, Rule.class);
                RuleOperator ruleOperator = RuleOperator.fromStr(rule.op());
                // validate features (check for matching rules)
                validateCountryMatch(country, rule, ruleOperator, ruleMatch);
                validateAppVersionMatch(appVersion, rule, ruleOperator, ruleMatch);
                validateTierMatch(tier, rule, ruleOperator, ruleMatch);
            }
        }
        return ruleMatch;
    }

    private static void validateTierMatch(String tier, Rule rule, RuleOperator ruleOperator, List<Rule> ruleMatch) {
        if (rule.attr().equalsIgnoreCase(FeatureType.TIER.name())) {
            switch (ruleOperator) {
                case RuleOperator.IN:
                case RuleOperator.EQ:
                    if (rule.values().containsValue(tier)) {
                        ruleMatch.add(rule);// append Rule
                    }
                    break;
                default:
                    LOGGER.warn("Tier OP not supported: {}", rule.op());
                    break;
            }
        }
    }

    private static void validateAppVersionMatch(String appVersion, Rule rule, RuleOperator ruleOperator, List<Rule> ruleMatch) {
        if (rule.attr().equalsIgnoreCase(FeatureType.APPVERSION.name())) {
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

    private static void validateCountryMatch(String country, Rule rule, RuleOperator ruleOperator, List<Rule> ruleMatch) {
        if (rule.attr().equalsIgnoreCase(FeatureType.COUNTRY.name())) {
            switch (ruleOperator) {
                case RuleOperator.IN:
                case RuleOperator.EQ:
                    if (rule.values().containsValue(country)) {
                        ruleMatch.add(rule);// append Rule
                    }
                    break;
                default:
                    LOGGER.warn("Country OP not supported: {}", rule.op());
                    break;
            }
        }
    }
}
