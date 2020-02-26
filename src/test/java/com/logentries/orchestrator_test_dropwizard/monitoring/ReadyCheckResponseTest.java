package com.logentries.orchestrator_test_dropwizard.monitoring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReadyCheckResponseTest {
    @Test
    @DisplayName("Successful readycheck")
    public void testBuildWithDependenciesReady() {
        final ReadyCheckResponse.Builder builder = new ReadyCheckResponse.Builder();
        builder.dependency("cigarettes", true);
        builder.dependency("alcohol", true);
        builder.dependency("rollerblading", true);

        final ReadyCheckResponse response = builder.build();

        assertTrue(response.isReady());
        assertEquals(3, response.getDependencies().size());
        assertTrue(response.getDependencies().get("cigarettes"));
        assertTrue(response.getDependencies().get("alcohol"));
        assertTrue(response.getDependencies().get("rollerblading"));
    }

    @Test
    @DisplayName("Unsuccessful readycheck - very unhealthy")
    public void testBuildWithDependenciesNotReady() {
        final ReadyCheckResponse.Builder builder = new ReadyCheckResponse.Builder();
        builder.dependency("cigarettes", false);
        builder.dependency("alcohol", false);
        builder.dependency("rollerblading", false);

        final ReadyCheckResponse response = builder.build();

        assertFalse(response.isReady());
        assertEquals(3, response.getDependencies().size());
        assertFalse(response.getDependencies().get("cigarettes"));
        assertFalse(response.getDependencies().get("alcohol"));
        assertFalse(response.getDependencies().get("rollerblading"));
    }

    @Test
    @DisplayName("Unsuccessful readycheck - partially healthy")
    public void testBuildWithDependenciesMixed() {
        final ReadyCheckResponse.Builder builder = new ReadyCheckResponse.Builder();
        builder.dependency("cigarettes", true);
        builder.dependency("alcohol", false);
        builder.dependency("rollerblading", true);

        final ReadyCheckResponse response = builder.build();

        assertFalse(response.isReady());
        assertEquals(3, response.getDependencies().size());
        assertTrue(response.getDependencies().get("cigarettes"));
        assertFalse(response.getDependencies().get("alcohol"));
        assertTrue(response.getDependencies().get("rollerblading"));
    }

    @Test
    @DisplayName("Successful readycheck with no dependencies")
    public void testBuildWithoutDependencies() {
        final ReadyCheckResponse response = new ReadyCheckResponse.Builder().build();

        assertTrue(response.isReady());
        assertEquals(0, response.getDependencies().size());
    }
}
