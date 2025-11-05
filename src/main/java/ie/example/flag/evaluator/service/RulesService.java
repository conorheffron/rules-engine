package ie.example.flag.evaluator.service;

import module java.base;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ie.example.flag.evaluator.dto.Feature;
import ie.example.flag.evaluator.dto.Rule;
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

    private Map<String, Feature> featuresById = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    @Autowired
    public RulesService(Environment environment) {
        this.environment = environment;
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

            if (feature.getRuleGroups() != null) {
                Map<String, Map<String, Object>>  featureAllRules = objectMapper
                        .convertValue(feature.getRuleGroups().getAll(), Map.class);
                if (featureAllRules != null && !featureAllRules.isEmpty()) {
                    for (Map<String, Object> ruleMap : featureAllRules.values()) {
                        Rule rule = objectMapper.convertValue(ruleMap, Rule.class);
                        LOGGER.info(String.format("----------: Rule info for feature set ALL %s is %s", featureKey, rule.toString()));
                    }
                }

                Map<String, Map<String, Object>>  featureAnyRules = objectMapper
                        .convertValue(feature.getRuleGroups().getAny(), Map.class);
                if (featureAnyRules != null && !featureAnyRules.isEmpty()) {
                    for (Map<String, Object> ruleMap : featureAnyRules.values()) {
                        Rule rule = objectMapper.convertValue(ruleMap, Rule.class);
                        LOGGER.info("----------: Rule info" + rule.toString());
                        LOGGER.info(String.format("----------: Rule info for feature set ANY %s is %s", featureKey, rule.toString()));
                    }
                }
            }
        }

    }

    public Map<String, Feature> getFeaturesById() {
        return featuresById;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public List<Rule> rulesMatcher(String feature, String country, String appVersion, Map<String,
            Map<String, Object>> featureAllRules) {
        List<Rule> ruleMatch = new ArrayList<>();
        if (featureAllRules != null && !featureAllRules.isEmpty()) {
            for (Map<String, Object> ruleMap : featureAllRules.values()) {
                Rule rule = this.objectMapper.convertValue(ruleMap, Rule.class);
                LOGGER.info(String.format("----------: Rule info for feature set ALL %s is %s", feature, rule.toString()));
                if (rule.getAttr().equalsIgnoreCase("country")) {
                    switch (rule.getOp()) {
                        case "IN":
                            if (rule.getValues().containsValue(country)) {
                                ruleMatch.add(rule);// append Rule
                            }
                            break;
                        default:
                            LOGGER.warn("Country OP not supported: " + rule.getOp());
                            break;
                    }
                }
                if (rule.getAttr().equalsIgnoreCase("appVersion")) {
                    switch (rule.getOp()) {
                        case "GTE":
                            Optional<String> appVersionOptional = rule.getValues().values().stream().findFirst();
                            int vers = Integer.parseInt(appVersionOptional.get());
                            if (Integer.parseInt(appVersion) >= vers) {
                                ruleMatch.add(rule);// append Rule
                            }
                            break;
                        default:
                            LOGGER.warn("Version OP not supported: " + rule.getOp());
                            break;
                    }
                }
            }
        }
        return ruleMatch;
    }
}
