package ie.example.flag.evaluator.dto;

import java.util.Map;

public class RuleGroups {
    private Map<String, Map<String, Object>> all;
    private Map<String, Map<String, Object>> any;

    public Map<String, Map<String, Object>> getAll() {
        return all;
    }

    public void setAll(Map<String, Map<String, Object>> all) {
        this.all = all;
    }

    public Map<String, Map<String, Object>> getAny() {
        return any;
    }

    public void setAny(Map<String, Map<String, Object>> any) {
        this.any = any;
    }
}
