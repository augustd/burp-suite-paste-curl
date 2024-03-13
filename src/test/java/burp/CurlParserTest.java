package burp;

import burp.api.montoya.http.message.HttpHeader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurlParserTest {

    @Test
    public void parseQuoted() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl 'https://company.co/api/whoami' -H 'Authorization: Bearer XXX'");

        assertNotNull(request);
        assertEquals("https", request.getProtocol());
        assertEquals("company.co", request.getHost());
        assertEquals("/api/whoami", request.getPath());

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
    public void parseExplicitPut() {
        CurlParser.CurlRequest request = CurlParser.parseCurlCommand("curl -X PUT 'https://example.com/api/endpoint' -H 'Content-Type: application/json' --data-raw '{\"key\": \"value\"}'");

        assertNotNull(request);
        assertEquals("PUT", request.getMethod());
        assertEquals("https", request.getProtocol());
        assertEquals("example.com", request.getHost());
        assertEquals("/api/endpoint", request.getPath());

        // Test headers
        List<HttpHeader> headers = request.getHeaders();
        assertEquals(1, headers.size());

        HttpHeader header = headers.get(0);
        assertEquals("Content-Type", header.name());
        assertEquals("application/json", header.value());

        //test body
        assertEquals("{\"key\": \"value\"}", request.getBody());

    }

}