package net.ironoc.rules.engine.delegate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ironoc.rules.engine.dto.Feature;
import net.ironoc.rules.engine.dto.Rule;
import net.ironoc.rules.engine.service.RulesService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.ironoc.rules.engine.delegate.FeatureEnabledDelegate.VAR_FEATURE;

@Component
public class RulesAggregatorDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(RulesAggregatorDelegate.class);

    public static final String VAR_RULES_JSON = "rulesJson";

    private final ObjectMapper objectMapper;
    private final RulesService rulesService;

    public RulesAggregatorDelegate(ObjectMapper objectMapper, RulesService rulesService) {
        this.objectMapper = objectMapper;
        this.rulesService = rulesService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        boolean skip = Boolean.TRUE.equals(execution.getVariable(PassEngineDelegate.VAR_SKIP_RULES_ENGINE));
        boolean featureEnabled = Boolean.TRUE.equals(execution.getVariable(FeatureEnabledDelegate.VAR_FEATURE_ENABLED));

        List<Rule> matchedRules;

        if (skip || !featureEnabled) {
            matchedRules = new ArrayList<>();
        } else {
            String country = Objects.toString(execution.getVariable(FeatureDelegate.VAR_COUNTRY), "").trim();
            String appVersion = Objects.toString(execution.getVariable(AppVersionDelegate.VAR_APP_VERSION), "").trim();
            String tier = Objects.toString(execution.getVariable(TierDelegate.VAR_TIER), "").trim();

            String featureId = Objects.toString(execution.getVariable(VAR_FEATURE), "").trim();
            Feature ft = featureId.isEmpty() ? null : rulesService.getFeaturesById().get(featureId);
            List<Rule> allRuleMatch = rulesService.getAllRuleMatch(country, appVersion, tier, ft);
            List<Rule> anyRuleMatch = rulesService.getAnyRuleMatch(country, appVersion, tier, ft);
            matchedRules = Objects.requireNonNull(rulesService.createResponseFromMatches(featureId, allRuleMatch, anyRuleMatch).getBody()).rules();
        }

        execution.setVariable(PassEngineDelegate.VAR_MATCHED_RULES, matchedRules);

        try {
            String json = objectMapper.writeValueAsString(matchedRules);
            execution.setVariable(VAR_RULES_JSON, json);
            LOGGER.info("Aggregated {} matched rules into rulesJson.", matchedRules.size());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize matched rules to JSON", e);
        }
    }
}