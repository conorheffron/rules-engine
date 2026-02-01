package net.ironoc.rules.engine.delegate;

import net.ironoc.rules.engine.dto.Feature;
import net.ironoc.rules.engine.service.DetailCacheI;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FeatureEnabledDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureEnabledDelegate.class);

    public static final String VAR_FEATURE = "feature";
    public static final String VAR_FEATURE_ENABLED = "featureEnabled";
    public static final String VAR_FEATURE_DTO = "featureDto";
    public static final String VAR_RULE_GROUPS_ALL = "ruleGroupsAll";
    public static final String VAR_RULE_GROUPS_ANY = "ruleGroupsAny";

    private final DetailCacheI featureDetailsService;

    public FeatureEnabledDelegate(DetailCacheI featureDetailsService) {
        this.featureDetailsService = featureDetailsService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        // TODO
        String featureId = Objects.toString(execution.getVariable(VAR_FEATURE), "").trim();

        Feature feature = featureId.isEmpty() ? null : featureDetailsService.getFeaturesById().get(featureId);
        boolean enabled = feature != null && feature.enabled();

        execution.setVariable(VAR_FEATURE_ENABLED, enabled);

        if (enabled) {
            execution.setVariable(VAR_FEATURE_DTO, feature);
            execution.setVariable(VAR_RULE_GROUPS_ALL, feature.ruleGroups() != null ? feature.ruleGroups().all() : null);
            execution.setVariable(VAR_RULE_GROUPS_ANY, feature.ruleGroups() != null ? feature.ruleGroups().any() : null);
            LOGGER.info("Feature '{}' is enabled; loaded rule groups into process context.", featureId);
        } else {
            execution.removeVariable(VAR_FEATURE_DTO);
            execution.removeVariable(VAR_RULE_GROUPS_ALL);
            execution.removeVariable(VAR_RULE_GROUPS_ANY);
            LOGGER.info("Feature '{}' is disabled or missing; cleared rule context.", featureId);
        }
    }
}