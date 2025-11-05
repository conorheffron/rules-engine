package ie.example.flag.evaluator.dto;

import java.util.Map;

public class Rule {
    private String attr;
    private String op;
    private Map<String, String> values;

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "attr='" + attr + '\'' +
                ", op='" + op + '\'' +
                ", values=" + values +
                '}';
    }
}
