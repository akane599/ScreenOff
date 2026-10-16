package com.tile.screenoff;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {
    public static final int MAX_HEADER_BYTES = 8 * 1024;
    private static final byte SP = 0x20;
    private static final byte LF = 0x0a;
    private static final byte CR = 0x0d;

    private byte[] rawData = new byte[]{};
    private boolean complete;
    private boolean tooLarge;

    public void add(byte[] data) {
        if (complete || tooLarge || data == null || data.length == 0) {
            return;
        }

        int remaining = MAX_HEADER_BYTES - rawData.length;
        int copyLength = Math.min(remaining, data.length);
        byte[] newBytes = Arrays.copyOf(rawData, rawData.length + copyLength);
        System.arraycopy(data, 0, newBytes, rawData.length, copyLength);
        rawData = newBytes;

        int end = findHeaderEnd(rawData);
        if (end >= 0) {
            if (end <= MAX_HEADER_BYTES) {
                rawData = Arrays.copyOf(rawData, end);
                complete = true;
            } else {
                tooLarge = true;
            }
        } else if (copyLength < data.length || rawData.length == MAX_HEADER_BYTES) {
            tooLarge = true;
        }
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isTooLarge() {
        return tooLarge;
    }

    public void clear() {
        rawData = new byte[]{};
        complete = false;
        tooLarge = false;
    }

    public HttpRequest parse() {
        if (!complete) {
            return null;
        }
        String[] lines = splitLines(rawData);
        if (lines == null || lines.length == 0) return null;
        HttpRequest.StartLine startLine = parseStartLine(lines[0]);
        if (startLine == null) return null;
        int emptyLineNum = findEmptyLine(lines);
        String[] headerField = Arrays.copyOfRange(lines, 1, emptyLineNum);
        Map<String, String> headers = parseRequestHeader(headerField);
        return new HttpRequest(startLine, headers);
    }

    private HttpRequest.StartLine parseStartLine(String startLine) {
        if (startLine == null) {
            return null;
        }

        String[] splitLine = startLine.split(new String(new byte[]{SP}));
        if(splitLine.length != 3) {
            return null;
        }

        return new HttpRequest.StartLine(splitLine[1]);
    }

    private Map<String, String> parseRequestHeader(String[] headerField) {
        if(headerField == null || headerField.length == 0) {
            return new HashMap<>();
        }

        Map<String, String> result = new HashMap<>();

        for (String header : headerField) {
            String[] splitHeader = header.split(":", 2);
            if (splitHeader.length != 2) {
                continue;
            }
            result.put(splitHeader[0], splitHeader[1].trim());
        }

        return result;
    }

    private String[] splitLines(byte[] requestData) {
        if (requestData == null || requestData.length == 0) {
            return null;
        }
        if (requestData[0] == CR || requestData[0] == LF) {
            return null;
        }

        String dataStr = new String(requestData, StandardCharsets.US_ASCII);
        return dataStr.split(new String(new byte[]{CR, LF}));
    }

    private int findEmptyLine(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                return i;
            }
        }
        return lines.length;
    }

    private int findHeaderEnd(byte[] data) {
        for (int i = 3; i < data.length; i++) {
            if (data[i - 3] == CR && data[i - 2] == LF && data[i - 1] == CR && data[i] == LF) {
                return i + 1;
            }
        }
        return -1;
    }
}
