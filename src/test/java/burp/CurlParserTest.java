package burp;

import burp.api.montoya.http.message.HttpHeader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurlParserTest {

    @Test
    public void parseLocalhost() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl http://localhost:8000");

        assertNotNull(request);
        assertEquals("http", request.getProtocol());
        assertEquals("localhost", request.getHost());
        assertEquals("/", request.getPath());
        assertEquals(8000, request.getPort());

        assertEquals("http://localhost:8000/", request.getBaseUrl());
    }

    @Test
    public void parseQuoted() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl 'https://company.co/api/whoami' -H 'Authorization: Bearer XXX'");

        assertNotNull(request);
        assertEquals("https", request.getProtocol());
        assertEquals("company.co", request.getHost());
        assertEquals("/api/whoami", request.getPath());

        assertEquals("https://company.co/api/whoami", request.getBaseUrl());

        // Test headers
        List<HttpHeader> headers = request.getHeaders();
        assertEquals(1, headers.size());

        HttpHeader header = headers.get(0);
        assertEquals("Authorization", header.name());
        assertEquals("Bearer XXX", header.value());
    }

    @Test
    public void parseDoubleQuoted() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl \"https://company.co/api/whoami\" -H \"Authorization: Bearer XXX\"");

        assertNotNull(request);
        assertEquals("https", request.getProtocol());
        assertEquals("company.co", request.getHost());
        assertEquals("/api/whoami", request.getPath());

        assertEquals("https://company.co/api/whoami", request.getBaseUrl());

        // Test headers
        List<HttpHeader> headers = request.getHeaders();
        assertEquals(1, headers.size());

        HttpHeader header = headers.get(0);
        assertEquals("Authorization", header.name());
        assertEquals("Bearer XXX", header.value());
    }

    @Test
    public void parseUnQuoted() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl https://company.co/api/whoami -H 'Authorization: Bearer XXX'");

        assertNotNull(request);
        assertEquals("https", request.getProtocol());
        assertEquals("company.co", request.getHost());
        assertEquals("/api/whoami", request.getPath());

        assertEquals("https://company.co/api/whoami", request.getBaseUrl());

        // Test headers
        List<HttpHeader> headers = request.getHeaders();
        assertEquals(1, headers.size());

        HttpHeader header = headers.get(0);
        assertEquals("Authorization", header.name());
        assertEquals("Bearer XXX", header.value());
    }

    @Test
    public void parseMultiHeader() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl https://company.co/api/endpoint -H 'Authorization: Bearer XXX' -H 'content-type: application/json' -H 'accept: application/json, text/plain, */*' ");

        assertEquals("https://company.co/api/endpoint", request.getBaseUrl());

        // Test headers
        List<HttpHeader> headers = request.getHeaders();
        assertEquals(3, headers.size());

        HttpHeader header0 = headers.get(0);
        assertEquals("Authorization", header0.name());
        assertEquals("Bearer XXX", header0.value());

        HttpHeader header1 = headers.get(1);
        assertEquals("content-type", header1.name());
        assertEquals("application/json", header1.value());

        HttpHeader header2 = headers.get(2);
        assertEquals("accept", header2.name());
        assertEquals("application/json, text/plain, */*", header2.value());
    }

    @Test
    public void parseImplicitPost() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl 'https://example.com/api/endpoint' -H 'Content-Type: application/json' --data-raw '{\"key\": \"value\"}'");

        assertNotNull(request);
        assertEquals("POST", request.getMethod());
        assertEquals("https", request.getProtocol());
        assertEquals("example.com", request.getHost());
        assertEquals("/api/endpoint", request.getPath());

        assertEquals("https://example.com/api/endpoint", request.getBaseUrl());

        // Test headers
        List<HttpHeader> headers = request.getHeaders();
        assertEquals(1, headers.size());

        HttpHeader header = headers.get(0);
        assertEquals("Content-Type", header.name());
        assertEquals("application/json", header.value());

        //test body
        assertEquals("{\"key\": \"value\"}", request.getBody());
    }

    @Test
    public void parseDoubleQuotedJson() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl 'https://example.com/api/endpoint' -H 'Content-Type: application/json' -d \"{'key': 'value'}\"");

        assertNotNull(request);
        assertEquals("POST", request.getMethod());
        assertEquals("https", request.getProtocol());
        assertEquals("example.com", request.getHost());
        assertEquals("/api/endpoint", request.getPath());

        assertEquals("https://example.com/api/endpoint", request.getBaseUrl());

        // Test headers
        List<HttpHeader> headers = request.getHeaders();
        assertEquals(1, headers.size());

        HttpHeader header = headers.get(0);
        assertEquals("Content-Type", header.name());
        assertEquals("application/json", header.value());

        //test body
        assertEquals("{'key': 'value'}", request.getBody());
    }

    @Test
    public void parseExplicitPut() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl -X PUT 'https://example.com/api/endpoint' -H 'Content-Type: application/json' --data-raw '{\"key\": \"value\"}'");

        assertNotNull(request);
        assertEquals("PUT", request.getMethod());
        assertEquals("https", request.getProtocol());
        assertEquals("example.com", request.getHost());
        assertEquals("/api/endpoint", request.getPath());

        assertEquals("https://example.com/api/endpoint", request.getBaseUrl());

        // Test headers
        List<HttpHeader> headers = request.getHeaders();
        assertEquals(1, headers.size());

        HttpHeader header = headers.get(0);
        assertEquals("Content-Type", header.name());
        assertEquals("application/json", header.value());

        //test body
        assertEquals("{\"key\": \"value\"}", request.getBody());

    }

    @Test
    public void parseExplicitGet() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl -X \"GET\" \"https://api.sendgrid.com/v3/templates\" -H \"Authorization: Bearer Your.API.Key-HERE\" -H \"Content-Type: application/json\"");

        assertNotNull(request);
        assertEquals("GET", request.getMethod());
        assertEquals("https", request.getProtocol());
        assertEquals("api.sendgrid.com", request.getHost());
        assertEquals("/v3/templates", request.getPath());

        assertEquals("https://api.sendgrid.com/v3/templates", request.getBaseUrl());

        // Test headers
        List<HttpHeader> headers = request.getHeaders();
        assertEquals(2, headers.size());

        HttpHeader header0 = headers.get(0);
        assertEquals("Authorization", header0.name());
        assertEquals("Bearer Your.API.Key-HERE", header0.value());

        HttpHeader header1 = headers.get(1);
        assertEquals("Content-Type", header1.name());
        assertEquals("application/json", header1.value());
    }

    @Test
    public void parseGithubMultiline() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl -L \\\n" +
                "  -H \"Accept: application/vnd.github+json\" \\\n" +
                "  -H \"Authorization: Bearer <YOUR-TOKEN>\" \\\n" +
                "  -H \"X-GitHub-Api-Version: 2022-11-28\" \\\n" +
                "  https://api.github.com/repos/OWNER/REPO/contents/PATH");

        assertNotNull(request);
        assertEquals("GET", request.getMethod());
        assertEquals("https", request.getProtocol());
        assertEquals("api.github.com", request.getHost());
        assertEquals("/repos/OWNER/REPO/contents/PATH", request.getPath());

        assertEquals("https://api.github.com/repos/OWNER/REPO/contents/PATH", request.getBaseUrl());

        // Test headers
        List<HttpHeader> headers = request.getHeaders();
        assertEquals(3, headers.size());

        HttpHeader header0 = headers.get(0);
        assertEquals("Accept", header0.name());
        assertEquals("application/vnd.github+json", header0.value());

        HttpHeader header1 = headers.get(1);
        assertEquals("Authorization", header1.name());
        assertEquals("Bearer <YOUR-TOKEN>", header1.value());

        HttpHeader header2 = headers.get(2);
        assertEquals("X-GitHub-Api-Version", header2.name());
        assertEquals("2022-11-28", header2.value());
    }

    @Test
    public void parseAmplitudeMultiline() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("""
    curl --location --request POST 'https://api.amplitude.com/2/httpapi' \\
    --header 'Content-Type: application/json' \\
    --data-raw '{
        "api_key": "YOUR_API_KEY",
        "events": [
            {
                "user_id": "12345",
                "event_type": "watch_tutorial",
                "user_properties": {
                    "Cohort": "Test A"
                },
                "country": "United States",
                "ip": "127.0.0.1",
                "time": 1396381378123
            }
        ]
    }'
    """);

        assertNotNull(request);
        assertEquals("POST", request.getMethod());
        assertEquals("https", request.getProtocol());
        assertEquals("api.amplitude.com", request.getHost());
        assertEquals("/2/httpapi", request.getPath());

        assertEquals("https://api.amplitude.com/2/httpapi", request.getBaseUrl());

        // Test headers
        List<HttpHeader> headers = request.getHeaders();
        assertEquals(1, headers.size());

        HttpHeader header0 = headers.get(0);
        assertEquals("Content-Type", header0.name());
        assertEquals("application/json", header0.value());
    }

    @Test
    public void parseGetWithQuery() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl --location -g --request POST 'https://api.company.com/v1/api?parameter=value&api_key=123456789'");

        assertNotNull(request);
        assertEquals("POST", request.getMethod());
        assertEquals("https", request.getProtocol());
        assertEquals("api.company.com", request.getHost());
        assertEquals("/v1/api", request.getPath());
        assertEquals("parameter=value&api_key=123456789", request.getQuery());

        assertEquals("https://api.company.com/v1/api?parameter=value&api_key=123456789", request.getBaseUrl());
    }
}