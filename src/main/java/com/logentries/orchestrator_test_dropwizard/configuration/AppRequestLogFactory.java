package com.logentries.orchestrator_test_dropwizard.configuration;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.logentries.core.metrics.LeMetrics;
import io.dropwizard.request.logging.RequestLogFactory;
import org.apache.http.HttpHeaders;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.concurrent.ThreadSafe;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;

/**
 * Provides the Jetty request logger. This logs a message for every request processed, with our customised format.
 */
@ThreadSafe
public final class AppRequestLogFactory implements RequestLogFactory {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public RequestLog build(final String name) {
        return new AppRequestLog();
    }

    @VisibleForTesting
    static final class AppRequestLog implements RequestLog {
        private static final Logger LOGGER = LoggerFactory.getLogger(AppRequestLog.class);
        private static final String NULL_VALUE = "null";

        @VisibleForTesting
        static final AtomicLong LAST_SUCCESSFUL_HEALTH_CHECK = new AtomicLong();

        @Override
        public void log(final Request request, final Response response) {
            final long requestLatency = (System.currentTimeMillis() - request.getTimeStamp());
            final int statusCode = response.getStatus();
            final String path = request.getPathInfo();
            final String method = request.getMethod();
            final String userAgent = request.getHeader(HttpHeaders.USER_AGENT);

            if ("/healthcheck".equals(path) || "/readycheck".equals(path)) {
                if (statusCode != SC_OK) {
                    final long timeDiff = (new Date().getTime() - LAST_SUCCESSFUL_HEALTH_CHECK.get());
                    LOGGER.error("HealthCheck failure. status_code={} time_diff_ms={}",
                            statusCode, timeDiff);
                } else {
                    LAST_SUCCESSFUL_HEALTH_CHECK.set(new Date().getTime());
                }
            } else {
                LOGGER.info("RequestLog r7_correlation_id={} r7_consumer={} response_code={} referrer={} user_agent={} response_time={}" +
                                " remote_address={} x_forwarded_for={} method={} path={}",
                        getEscapedHeader(request, "R7-Correlation-Id"),
                        getEscapedHeader(request, "R7-Consumer"),
                        statusCode,
                        getEscapedHeader(request, HttpHeaders.REFERER),
                        userAgent,
                        requestLatency,
                        request.getRemoteAddr(),
                        getEscapedHeader(request, "X-Forwarded-For"),
                        method,
                        "\"" + path + "\"");
            }
            // Send some metrics to Datadog
            final Map<String, String> datadogTags = ImmutableMap.of(
                    "response_code", String.valueOf(statusCode),
                    "method", method);
            LeMetrics.timer("endpoint", datadogTags).update(requestLatency, TimeUnit.MILLISECONDS);
        }

        @VisibleForTesting
        String getEscapedHeader(final Request request, final String headerName) {
            final String value = request.getHeader(headerName);

            if (Strings.isNullOrEmpty(value)) {
                return NULL_VALUE;
            } else {
                return "\"" + escapeJava(value) + "\"";
            }
        }
    }
}
