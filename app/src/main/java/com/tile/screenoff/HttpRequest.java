package com.tile.screenoff;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

public class HttpRequest {
    private final StartLine startLine;
    private final Map<String, String> headers;

    public HttpRequest(StartLine startLine, Map<String, String> headers) {
        this.startLine = startLine;
        this.headers = headers;
    }

    public String getRequestTarget() {
        return startLine.requestTarget;
    }

    public String getPath() {
        int queryStart = startLine.requestTarget.indexOf('?');
        return queryStart >= 0 ? startLine.requestTarget.substring(0, queryStart) : startLine.requestTarget;
    }

    public boolean isAuthorized(String expectedToken) {
        String suppliedToken = getQueryParameter("t");
        return expectedToken != null && suppliedToken != null
                && MessageDigest.isEqual(expectedToken.getBytes(StandardCharsets.US_ASCII),
                suppliedToken.getBytes(StandardCharsets.US_ASCII));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    private String getQueryParameter(String name) {
        int queryStart = startLine.requestTarget.indexOf('?');
        if (queryStart < 0 || queryStart == startLine.requestTarget.length() - 1) {
            return null;
        }
        String[] parameters = startLine.requestTarget.substring(queryStart + 1).split("&");
        for (String parameter : parameters) {
            int separator = parameter.indexOf('=');
            if (separator >= 0 && name.equals(parameter.substring(0, separator))) {
                return parameter.substring(separator + 1);
            }
        }
        return null;
    }

    public static class StartLine {
        private final String requestTarget;

        public StartLine(String requestTarget) {
            this.requestTarget = requestTarget;
        }
    }
}
