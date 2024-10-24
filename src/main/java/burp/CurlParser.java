package burp;

import burp.api.montoya.http.message.HttpHeader;

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

        String requestMethod = "GET";
        String protocol = null;
        String host = null;
        String path = null;
        List<HttpHeader> headers = new ArrayList<>();
        String body = "";

        // Extract request method
        Pattern methodPattern = Pattern.compile("-X\\s+([A-Z]+)");
        Matcher methodMatcher = methodPattern.matcher(curlCommand);

        if (methodMatcher.find()) {
            requestMethod = methodMatcher.group(1);
        }

        // Extract protocol
        Pattern protocolPattern = Pattern.compile("(https?)://");
        Matcher protocolMatcher = protocolPattern.matcher(curlCommand);
        if (protocolMatcher.find()) {
            protocol = protocolMatcher.group(1);
        }

        // Extract host
        Pattern hostPattern = Pattern.compile("['\"]?[^:]+://([^/]+)/");
        Matcher hostMatcher = hostPattern.matcher(curlCommand);
        if (hostMatcher.find()) {
            host = hostMatcher.group(1);
        }

        // Extract path
        Pattern pathPattern = Pattern.compile("['\"]?[^:]+://[^/]+(/[^'\" ]+)(?:['\"]|(?=\\s|$))");
        Matcher pathMatcher = pathPattern.matcher(curlCommand);
        if (pathMatcher.find()) {
            path = pathMatcher.group(1).trim();
        }

        // Extract headers
        Pattern headerPattern = Pattern.compile("-H ['\"]?([^'\"]+)['\"]?");
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
        Pattern bodyPattern = Pattern.compile("(?:--data-raw|-d)\\s+(['\"])(.*?)(\\1)");
        Matcher bodyMatcher = bodyPattern.matcher(curlCommand);

        if (bodyMatcher.find()) {
            body = bodyMatcher.group(2);
            // If -X option is not specified and --data-raw is present, assume it's a POST request
            if (requestMethod == null || "GET".equals(requestMethod)) {
                requestMethod = "POST";
            }
        }

        if (host != null && path != null) {
            return new CurlRequest(requestMethod, protocol, host, path, headers, body);
        } else {
            return null;
        }
    }

    static class CurlRequest {
        private final String method;
        private final String protocol;
        private final String host;
        private final String path;
        private final List<HttpHeader> headers;
        private final String body;

        public CurlRequest(String method, String protocol, String host, String path, List<HttpHeader> headers, String body) {
            this.method = method;
            this.protocol = protocol;
            this.host = host;
            this.path = path;
            this.headers = headers;
            this.body = body;
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
            return path;
        }

        public List<HttpHeader> getHeaders() {
            return headers;
        }

        public String getBody() {
            return body;
        }
    }
}
