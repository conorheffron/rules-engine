package ie.example.flag.evaluator.enums;

public enum RuleOperator {

    IN, EQ, GTE, GT;

    public static RuleOperator fromStr(String ruleOperator) throws IllegalArgumentException {
        try {
            return RuleOperator.valueOf(ruleOperator.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid operator: " + ruleOperator, e);
        }
    }
}
