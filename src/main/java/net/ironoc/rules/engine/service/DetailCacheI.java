package net.ironoc.rules.engine.service;

import net.ironoc.rules.engine.dto.Feature;

import java.util.Map;

public interface DetailCacheI {
    Map<String, Feature> getFeaturesById();

    void put(String featureId, Feature feature);

    void clear();
}
