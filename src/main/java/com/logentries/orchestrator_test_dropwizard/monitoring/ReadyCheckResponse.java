package com.logentries.orchestrator_test_dropwizard.monitoring;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Response to indicate the application's state of readiness.
 */
public final class ReadyCheckResponse {
    @JsonProperty("ready")
    private final boolean ready;

    @JsonProperty("dependencies")
    private final Map<String, Boolean> dependencies;

    private ReadyCheckResponse(final boolean ready, final Map<String, Boolean> dependencies) {
        this.ready = ready;
        this.dependencies = dependencies;
    }

    public static final class Builder {
        private final Map<String, Boolean> dependencies = new HashMap<>();

        public Builder dependency(final String name, final boolean status) {
            this.dependencies.put(name, Boolean.valueOf(status));
            return this;
        }

        public ReadyCheckResponse build() {
            return new ReadyCheckResponse(!dependencies.values().contains(Boolean.FALSE), ImmutableMap.copyOf(dependencies));
        }
    }

    public boolean isReady() {
        return ready;
    }

    public Map<String, Boolean> getDependencies() {
        return dependencies;
    }
}
