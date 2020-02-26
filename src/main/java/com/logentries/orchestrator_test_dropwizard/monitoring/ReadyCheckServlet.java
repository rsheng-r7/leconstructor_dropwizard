package com.logentries.orchestrator_test_dropwizard.monitoring;

import com.codahale.metrics.servlets.AdminServlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE;

public class ReadyCheckServlet extends AdminServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadyCheckServlet.class);

    private final ObjectMapper mapper;

    public ReadyCheckServlet() {
        this.mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        ReadyCheckResponse response = this.checkDependencies();
        resp.setContentType("application/json");
        resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        if (response.isReady()) {
            resp.setStatus(SC_OK);
        } else {
            LOGGER.warn("Service not ready yet, some of the dependencies are not available. Dependencies: {}",
                    Joiner.on("\n").withKeyValueSeparator(": ready =").join(response.getDependencies()));
            resp.setStatus(SC_SERVICE_UNAVAILABLE);
        }

        try (OutputStream output = resp.getOutputStream()) {
            mapper.writeValue(output, response);
        } catch (IOException ex) {
            LOGGER.error("Failed to write ready check response: {}", ex.getMessage(), ex);
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }

    private ReadyCheckResponse checkDependencies() {
        // Check all service dependencies here
        return new ReadyCheckResponse.Builder().build();
    }

}
