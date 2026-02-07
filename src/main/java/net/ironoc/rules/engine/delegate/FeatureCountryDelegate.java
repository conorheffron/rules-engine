package net.ironoc.rules.engine.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FeatureCountryDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureDelegate.class);

    public static final String VAR_COUNTRY = "country";

    @Override
    public void execute(DelegateExecution execution) {
        // TODO
        String country = Objects.toString(execution.getVariable(VAR_COUNTRY), "").trim();
        LOGGER.info("Country captured='{}' (matching is performed in RulesAggregatorDelegate via RulesService).", country);
    }
}
