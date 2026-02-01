package net.ironoc.rules.engine.domain;

import module java.base;

import net.ironoc.rules.engine.dto.Rule;

public record ApiResponse(List<Rule> rules) {
}
