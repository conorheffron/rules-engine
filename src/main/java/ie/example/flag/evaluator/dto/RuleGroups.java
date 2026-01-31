package ie.example.flag.evaluator.dto;

import java.util.Map;

public record RuleGroups(Map<String, Map<String, Object>> all, Map<String, Map<String, Object>> any) {
}
