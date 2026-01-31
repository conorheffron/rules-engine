package ie.example.flag.evaluator.dto;

public record Feature(boolean enabled, RuleGroups ruleGroups) {

    @Override
    public String toString() {
        return "Feature{" +
                "enabled=" + enabled +
                ", ruleGroups=" + ruleGroups +
                '}';
    }
}
