package burp;

import burp.api.montoya.http.message.HttpHeader;

public class HttpHeaderImpl implements HttpHeader {

    private String name;
    private String value;

    public HttpHeaderImpl(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }
}
