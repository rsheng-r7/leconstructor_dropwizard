package com.logentries.orchestrator_test_dropwizard;

import com.logentries.core.metrics.LeMetrics;
import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.server.DefaultServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jdk7.Jdk7Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

import com.logentries.orchestrator_test_dropwizard.configuration.AppConfiguration;
import com.logentries.orchestrator_test_dropwizard.configuration.AppRequestLogFactory;
import com.logentries.orchestrator_test_dropwizard.monitoring.ReadyCheckServlet;
import com.logentries.orchestrator_test_dropwizard.monitoring.BaseHealthCheck;

/**
 * Application entry point.
 */
public class App extends Application<AppConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        ObjectMapper mapper = bootstrap.getObjectMapper();
        App.configureObjectMapper(mapper);
        mapper.registerModule(new Jdk8Module());
    }

    /**
     * Configures the Jackson ObjectMapper.
     * @param mapper: ObjectMapper instance to configure.
     */
    private static void configureObjectMapper(final ObjectMapper mapper) {
        final SimpleDateFormat defaultDateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        mapper.registerModule(new MrBeanModule());
        mapper.registerModule(new Jdk7Module());
        mapper.registerModule(new Jdk8Module());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setDateFormat(defaultDateFormat);
        mapper.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) {
        LOGGER.info("Application initialised, creating resources...");
        // Metrics
        manage(environment, LeMetrics::start, LeMetrics::stop);
        environment.metrics().addListener(new LeMetrics.LeMetricListener());
        // Monitoring
        environment.healthChecks().register("base", new BaseHealthCheck());
        environment.admin().addServlet("readycheck-servlet", new ReadyCheckServlet()).addMapping("/readycheck");
        //Requests log
        ((DefaultServerFactory) configuration.getServerFactory()).setRequestLogFactory(new AppRequestLogFactory());
    }

    private static void manage(Environment environment, Runnable start, Runnable stop) {
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {
                if (start != null) {
                    start.run();
                }
            }

            @Override
            public void stop() throws Exception {
                if (stop != null) {
                    stop.run();
                }
            }
        });
    }
}
