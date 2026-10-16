package com.tile.screenoff;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class HttpRequestTest {
    @Test
    public void exposesStartLineTargetAndHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost");
        HttpRequest request = new HttpRequest(new HttpRequest.StartLine("/index.html"), headers);

        assertEquals("/index.html", request.getRequestTarget());
        assertSame(headers, request.getHeaders());
    }
}
