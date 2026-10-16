package com.tile.screenoff;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class HttpRequestParserTest {
    @Test
    public void parseReturnsRequestTargetAndHeaders() {
        HttpRequestParser parser = new HttpRequestParser();
        parser.add(("GET /index.html?screen=on HTTP/1.1\r\n"
                + "Host: localhost\r\n"
                + "Connection: close\r\n\r\n").getBytes());

        HttpRequest request = parser.parse();

        assertEquals("/index.html?screen=on", request.getRequestTarget());
        Map<String, String> headers = request.getHeaders();
        assertEquals("localhost", headers.get("Host"));
        assertEquals("close", headers.get("Connection"));
    }

    @Test
    public void parsePreservesPowerControlQueryTarget() {
        HttpRequestParser parser = new HttpRequestParser();
        parser.add("GET /1?state=off HTTP/1.1\r\n\r\n".getBytes());

        HttpRequest request = parser.parse();

        assertEquals("/1?state=off", request.getRequestTarget());
    }

    @Test
    public void addAccumulatesRequestBytes() {
        HttpRequestParser parser = new HttpRequestParser();
        parser.add("GET / HTTP/1.1\r\nHost: local".getBytes());
        parser.add("host\r\n\r\n".getBytes());

        HttpRequest request = parser.parse();

        assertEquals("/", request.getRequestTarget());
        assertEquals("localhost", request.getHeaders().get("Host"));
    }

    @Test
    public void malformedHeadersAreIgnored() {
        HttpRequestParser parser = new HttpRequestParser();
        parser.add(("GET / HTTP/1.1\r\n"
                + "NotAHeader\r\n"
                + "X-Test: value\r\n\r\n").getBytes());

        Map<String, String> headers = parser.parse().getHeaders();

        assertFalse(headers.containsKey("NotAHeader"));
        assertEquals("value", headers.get("X-Test"));
    }

    @Test
    public void emptyInputReturnsNull() {
        assertNull(new HttpRequestParser().parse());
    }

    @Test
    public void leadingLineBreakReturnsNull() {
        HttpRequestParser parser = new HttpRequestParser();
        parser.add("\r\nGET / HTTP/1.1\r\n\r\n".getBytes());

        assertNull(parser.parse());
    }

    @Test
    public void malformedRequestLineReturnsNull() {
        HttpRequestParser parser = new HttpRequestParser();
        parser.add("GET /missing-version\r\n\r\n".getBytes());

        assertNull(parser.parse());
    }

    @Test
    public void clearDiscardsPreviouslyAddedBytes() {
        HttpRequestParser parser = new HttpRequestParser();
        parser.add("GET /stale HTTP/1.1\r\n\r\n".getBytes());
        parser.clear();
        parser.add("GET /fresh HTTP/1.1\r\n\r\n".getBytes());

        assertEquals("/fresh", parser.parse().getRequestTarget());
    }
}
