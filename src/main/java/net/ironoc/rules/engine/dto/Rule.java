package net.ironoc.rules.engine.dto;

import java.util.Map;

public record Rule(String attr, Map<String, String> values, String op) {

    @Override
    public String toString() {
        return "Rule{" +
                "attr='" + attr + '\'' +
                ", op='" + op + '\'' +
                ", values=" + values +
                '}';
    }
}
