package net.ironoc.rules.engine.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FeatureDisabledDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureDisabledDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        // TODO
        execution.setVariable(FeatureEnabledDelegate.VAR_FEATURE_ENABLED, false);

        execution.removeVariable(FeatureEnabledDelegate.VAR_FEATURE_DTO);
        execution.removeVariable(FeatureEnabledDelegate.VAR_RULE_GROUPS_ALL);
        execution.removeVariable(FeatureEnabledDelegate.VAR_RULE_GROUPS_ANY);

        LOGGER.info("Feature marked disabled; rule context cleared.");
    }
}