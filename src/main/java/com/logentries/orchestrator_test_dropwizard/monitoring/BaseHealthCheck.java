package com.logentries.orchestrator_test_dropwizard.monitoring;

import com.codahale.metrics.health.HealthCheck;

public class BaseHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
