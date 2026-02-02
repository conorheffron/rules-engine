package net.ironoc.rules.engine.service;

import net.ironoc.rules.engine.dto.Feature;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FeatureDetailService implements DetailCacheI {

    private final Map<String, Feature> featuresById = new ConcurrentHashMap<>();

    @Override
    public Map<String, Feature> getFeaturesById() {
        return featuresById;
    }

    @Override
    public void put(String featureId, Feature feature) {
        featuresById.put(featureId, feature);
    }

    @Override
    public void clear() {
        featuresById.clear();
    }
}
