package com.logentries.orchestrator_test_dropwizard.monitoring;

import org.apache.commons.lang.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

public class ReadyCheckServletTest {

    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    public void before() {
        this.request = mock(HttpServletRequest.class);
        this.response = mock(HttpServletResponse.class);
    }

    @Test
    @DisplayName("Successful servlet ready check")
    public void testSuccessfulReadyCheck() throws IOException {
        String expectedResponseBody = "{\"ready\":true,\"dependencies\":{}}";
        int bodyLength = 32;
        ServletOutputStream outputStreamMock = mock(ServletOutputStream.class);
        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        when(response.getOutputStream()).thenReturn(outputStreamMock);
        new ReadyCheckServlet().doGet(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(response, times(1)).setContentType("application/json");
        verify(response, times(1)).setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        verify(outputStreamMock, atLeastOnce()).close();
        verify(outputStreamMock, times(1)).write(captor.capture(), eq(0), eq(bodyLength));
        String actualResponseBody = new String(ArrayUtils.subarray(captor.getValue(), 0, bodyLength));
        assertEquals(expectedResponseBody, actualResponseBody);
    }


    @Test
    @DisplayName("Server error on ready check")
    public void testServerError() throws IOException {
        ServletOutputStream outputStreamMock = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStreamMock);
        doThrow(new IOException("something is wrong")).when(outputStreamMock).write(any(), anyInt(), anyInt());
        new ReadyCheckServlet().doGet(request, response);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(response, times(1)).setContentType("application/json");
        verify(response, times(1)).setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        verify(outputStreamMock, atLeastOnce()).close();
    }
}