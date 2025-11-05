package ie.example.flag.evaluator.dto;

public class Feature {
    private boolean enabled;
    private RuleGroups ruleGroups;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean value) { this.enabled = value; }

    public RuleGroups getRuleGroups() { return ruleGroups; }
    public void setRuleGroups(RuleGroups value) { this.ruleGroups = value; }

    @Override
    public String toString() {
        return "Feature{" +
                "enabled=" + enabled +
                ", ruleGroups=" + ruleGroups +
                '}';
    }
}
