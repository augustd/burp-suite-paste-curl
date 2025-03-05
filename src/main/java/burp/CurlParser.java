package burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses cURL request into strings.
 * 
 * @author August Detlefsen
 */
public class CurlParser {

    public static CurlRequest parseCurlCommand(String curlCommand) {
        return parseCurlCommand(curlCommand, null);
    }

    public static CurlRequest parseCurlCommand(String curlCommand, MontoyaApi api) {

        log("CurlParser.parseCurlCommand(): " + curlCommand, api);

        String requestMethod = "GET";
        String protocol = null;
        String host = null;
        String path = null;
        Integer port = null;
        String query = null;
        List<HttpHeader> headers = new ArrayList<>();
        String body = "";

        // Extract request method
        Pattern methodPattern = Pattern.compile("(?:--request|-X)\\s+([A-Z]+)");
        Matcher methodMatcher = methodPattern.matcher(curlCommand);
        if (methodMatcher.find()) {
            requestMethod = methodMatcher.group(1);
        }

        // Extract full URL
        Pattern pattern = Pattern.compile("([\\s'\"]?)(https?://.*?)");
        Matcher matcher = pattern.matcher(curlCommand);
        if (matcher.find()) {
            // Extract the delimiter
            String delimiter = matcher.group(1);

            // Check if the URL ends with the same delimiter
            if (delimiter != null && !delimiter.isEmpty()) {
                log("delimiter: |" + delimiter + "|", api);
                // Start looking after the delimiter
                int startIdx = matcher.end(1); // Skip the delimiter
                log("start: " + startIdx, api);
                int endIdx = startIdx;

                // Find the position where the URL ends
                for (int i = endIdx; i < curlCommand.length(); i++) {
                    if (curlCommand.charAt(i) == delimiter.charAt(0)) {
                        //endIdx = i;
                        break;
                    }
                    endIdx++;
                }

                log("end: " + endIdx, api);
                String extractedUrl = curlCommand.substring(startIdx, endIdx);
                log("url: " + extractedUrl, api);

                try {
                    URL url = new URL(extractedUrl);

                    protocol = url.getProtocol();
                    host = url.getHost();
                    path = url.getPath();
                    query = url.getQuery();
                    port = url.getPort();
                } catch (java.net.MalformedURLException mue) {
                    if (api != null) api.logging().logToError(mue);

                    return null;
                }
            }
        } else {
            return null;
        }

        // Extract headers
        Pattern headerPattern = Pattern.compile("(?:--header|-H)\\s+['\"]?([^'\"]+)['\"]?");
        Matcher headerMatcher = headerPattern.matcher(curlCommand);
        while (headerMatcher.find()) {
            String header = headerMatcher.group(1);
            int colonIndex = header.indexOf(':');
            if (colonIndex != -1) {
                String name = header.substring(0, colonIndex).trim();
                String value = header.substring(colonIndex + 1).trim();

                HttpHeader httpHeader = new HttpHeaderImpl(name, value);
                headers.add(httpHeader);
            }
        }

        // Extract request body
        Pattern bodyPattern = Pattern.compile("(?:--data-raw|-d)\\s+(['\"])(.*?)(\\1)", Pattern.DOTALL);
        Matcher bodyMatcher = bodyPattern.matcher(curlCommand);

        if (bodyMatcher.find()) {
            body = bodyMatcher.group(2);
            // If -X option is not specified and --data-raw is present, assume it's a POST request
            if (requestMethod == null || "GET".equals(requestMethod)) {
                requestMethod = "POST";
            }
        }

        if (api != null) {
            log("CurlParser.parseCurlCommand() complete: host: " + host + " path: " + path, api);
            log("Body: " + body, api);
        }

        if (host != null && path != null) {
            return new CurlRequest(requestMethod, protocol, host, path, query, port, headers, body);
        } else {
            return null;
        }
    }

    protected static void log(String toLog, MontoyaApi api) {
        if (api != null) {

        } else {
            System.out.println(toLog);
        }
    }

    static class CurlRequest {
        private final String method;
        private final String protocol;
        private final String host;
        private final String path;
        private final String query;
        private final Integer port;
        private final List<HttpHeader> headers;
        private final String body;

        public CurlRequest(String method, String protocol, String host, String path, String query, Integer port, List<HttpHeader> headers, String body) {
            this.method = method;
            this.protocol = protocol;
            this.host = host;
            this.path = path;
            this.query = query;
            this.port = port;
            this.headers = headers;
            this.body = body;
        }

        public String getBaseUrl() {
            StringBuilder builder = new StringBuilder();
            builder.append(getProtocol())
                   .append("://")
                   .append(getHost());

            if (port != -1 && port != 80 && port != 443) builder.append(":").append(getPort());

            builder.append(getPath());

            if (query != null && !"".equals(query)) builder.append("?").append(query);

            return builder.toString();
        }

        public String getMethod() {
            return method;
        }
        public String getProtocol() {
            return protocol;
        }
        public String getHost() {
            return host;
        }

        public String getPath() {
            if (path == null || "".equals(path)) return "/";

            return path;
        }

        public String getQuery() {
            return query;
        }

        public Integer getPort() {
            return port;
        }

        public List<HttpHeader> getHeaders() {
            return headers;
        }

        public String getBody() {
            return body;
        }
    }
}
