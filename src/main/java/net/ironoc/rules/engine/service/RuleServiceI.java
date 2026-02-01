package net.ironoc.rules.engine.service;

import net.ironoc.rules.engine.domain.ApiResponse;
import net.ironoc.rules.engine.dto.Feature;
import net.ironoc.rules.engine.dto.Rule;
import net.ironoc.rules.engine.enums.RuleGroup;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RuleServiceI extends JavaDelegate {
    @Override
    void execute(DelegateExecution execution);

    ResponseEntity<ApiResponse> createResponseFromMatches(String feature,
                                                          List<Rule> allRuleMatch,
                                                          List<Rule> anyRuleMatch);

    List<Rule> getRuleMatchByRuleGroup(String country, String appVersion, String tier,
                                       Feature ft, RuleGroup ruleGroup);
}
