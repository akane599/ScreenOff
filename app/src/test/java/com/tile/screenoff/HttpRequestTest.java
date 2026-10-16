package com.tile.screenoff;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HttpRequestTest {
    @Test
    public void exposesStartLineTargetAndHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost");
        HttpRequest request = new HttpRequest(new HttpRequest.StartLine("/index.html"), headers);

        assertEquals("/index.html", request.getRequestTarget());
        assertSame(headers, request.getHeaders());
    }

    @Test
    public void authorizationRequiresExactToken() {
        String token = "a-valid_token-0123456789";

        assertTrue(requestWithTarget("/?t=" + token).isAuthorized(token));
        assertFalse(requestWithTarget("/?t=wrong-token").isAuthorized(token));
        assertFalse(requestWithTarget("/").isAuthorized(token));
        assertFalse(requestWithTarget("/?t=" + token.substring(0, token.length() - 1)).isAuthorized(token));
    }

    private HttpRequest requestWithTarget(String target) {
        return new HttpRequest(new HttpRequest.StartLine(target), new HashMap<String, String>());
    }
}
