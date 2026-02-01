package net.ironoc.rules.engine.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AppVersionDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppVersionDelegate.class);

    public static final String VAR_APP_VERSION = "appVersion";

    @Override
    public void execute(DelegateExecution execution) {
        String appVersion = Objects.toString(execution.getVariable(VAR_APP_VERSION), "").trim();
        LOGGER.info("AppVersion captured='{}' (matching is performed in RulesAggregatorDelegate via RulesService).", appVersion);
    }
}