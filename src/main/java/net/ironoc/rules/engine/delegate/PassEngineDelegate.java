package net.ironoc.rules.engine.delegate;

import net.ironoc.rules.engine.dto.Rule;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PassEngineDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(PassEngineDelegate.class);

    public static final String VAR_MATCHED_RULES = "matchedRules";
    public static final String VAR_SKIP_RULES_ENGINE = "skipRulesEngine";

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable(VAR_SKIP_RULES_ENGINE, true);
        execution.setVariable(VAR_MATCHED_RULES, new ArrayList<Rule>());

        LOGGER.info("Bypassing rules engine: matchedRules set to empty and skipRulesEngine=true");
    }
}