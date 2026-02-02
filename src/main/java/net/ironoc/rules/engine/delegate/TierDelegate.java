package net.ironoc.rules.engine.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TierDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(TierDelegate.class);

    public static final String VAR_TIER = "tier";

    @Override
    public void execute(DelegateExecution execution) {
        // TODO
        String tier = Objects.toString(execution.getVariable(VAR_TIER), "").trim();
        LOGGER.info("Tier captured='{}' (matching is performed in RulesAggregatorDelegate via RulesService).", tier);
    }
}