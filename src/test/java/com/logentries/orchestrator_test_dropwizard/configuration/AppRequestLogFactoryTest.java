package com.logentries.orchestrator_test_dropwizard.configuration;

import org.apache.http.HttpHeaders;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class AppRequestLogFactoryTest {
    private Request request;
    private Response response;
    private AppRequestLogFactory.AppRequestLog requestLog;
    @Before
    public void before() throws Exception {
        this.request = mock(Request.class);
        this.response = mock(Response.class);
        this.requestLog= new AppRequestLogFactory.AppRequestLog();
    }

    @After
    public void after() {
        verifyNoMoreInteractions(request, response);
    }

    @Test
    public void testIsEnabled() {
        assertTrue(new AppRequestLogFactory().isEnabled());
    }

    @Test
    public void testBuild() {
        assertEquals(AppRequestLogFactory.AppRequestLog.class, new AppRequestLogFactory().build(null).getClass());
    }

    @Test
    public void testGetEscapedHeader() {
        final String header = "X-Some-Thing";

        when(request.getHeader(header)).thenReturn("a \"problematic\" string");

        assertEquals("\"a \\\"problematic\\\" string\"", requestLog.getEscapedHeader(request, header));

        verify(request, times(1)).getHeader(header);
    }

    @Test
    public void testGetEscapedHeaderNull() {
        final String header = "X-Some-Thing";

        when(request.getHeader(header)).thenReturn(null);

        assertEquals("null", requestLog.getEscapedHeader(request, header));

        verify(request, times(1)).getHeader(header);
    }

    @Test
    public void testGetEscapedHeaderEmpty() {
        final String header = "X-Some-Thing";

        when(request.getHeader(header)).thenReturn("");

        assertEquals("null", requestLog.getEscapedHeader(request, header));

        verify(request, times(1)).getHeader(header);
    }

    @Test
    public void testLogHealthCheckFirstTimeFailed() {
        when(response.getStatus()).thenReturn(SC_SERVICE_UNAVAILABLE);
        when(request.getPathInfo()).thenReturn("/healthcheck");
        when(request.getTimeStamp()).thenReturn(System.currentTimeMillis());

        requestLog.log(request, response);

        assertEquals(AppRequestLogFactory.AppRequestLog.LAST_SUCCESSFUL_HEALTH_CHECK.get(), 0L);

        verify(request, times(1)).getHeader(HttpHeaders.USER_AGENT);
        verify(request, times(1)).getMethod();
        verify(request, times(1)).getPathInfo();
        verify(request, times(1)).getTimeStamp();
        verify(response, times(1)).getStatus();
    }

    @Test
    public void testLogHealthCheckSuccess() {
        when(response.getStatus()).thenReturn(SC_OK);
        when(request.getPathInfo()).thenReturn("/healthcheck");
        when(request.getTimeStamp()).thenReturn(System.currentTimeMillis());

        requestLog.log(request, response);

        assertNotEquals(AppRequestLogFactory.AppRequestLog.LAST_SUCCESSFUL_HEALTH_CHECK.get(), 0L);

        verify(request, times(1)).getHeader(HttpHeaders.USER_AGENT);
        verify(request, times(1)).getMethod();
        verify(request, times(1)).getPathInfo();
        verify(request, times(1)).getTimeStamp();
        verify(response, times(1)).getStatus();
    }

    @Test
    public void testLogHealthCheckFailed() {
        AppRequestLogFactory.AppRequestLog.LAST_SUCCESSFUL_HEALTH_CHECK.set(0L);

        when(response.getStatus()).thenReturn(SC_SERVICE_UNAVAILABLE);
        when(request.getPathInfo()).thenReturn("/readycheck");
        when(request.getTimeStamp()).thenReturn(System.currentTimeMillis());

        requestLog.log(request, response);

        assertEquals(AppRequestLogFactory.AppRequestLog.LAST_SUCCESSFUL_HEALTH_CHECK.get(), 0L);

        verify(request, times(1)).getHeader(HttpHeaders.USER_AGENT);
        verify(request, times(1)).getMethod();
        verify(request, times(1)).getPathInfo();
        verify(request, times(1)).getTimeStamp();
        verify(response, times(1)).getStatus();
    }

    @Test
    public void testLogNoHealthCheck() {
        AppRequestLogFactory.AppRequestLog.LAST_SUCCESSFUL_HEALTH_CHECK.set(0L);

        when(response.getStatus()).thenReturn(SC_OK);
        when(request.getPathInfo()).thenReturn("/resource");
        when(request.getTimeStamp()).thenReturn(System.currentTimeMillis());

        requestLog.log(request, response);

        verify(request, times(1)).getPathInfo();
        verify(response, times(1)).getStatus();
        verify(request, times(1)).getHeader(HttpHeaders.USER_AGENT);
        verify(request, times(1)).getTimeStamp();
        verify(request, times(1)).getHeader(HttpHeaders.REFERER);
        verify(request, times(1)).getHeader("R7-Correlation-Id");
        verify(request, times(1)).getHeader("R7-Consumer");
        verify(request, times(1)).getHeader("X-Forwarded-For");
        verify(request, times(1)).getMethod();
        verify(request, times(1)).getRemoteAddr();
    }

}

