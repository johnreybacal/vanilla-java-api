package api.vanilla.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

public class Request {

    private final HttpExchange exchange;

    public Request(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public String getMethod() {
        return exchange.getRequestMethod();
    }

    public String getBody() throws IOException {
        StringBuilder buf;
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8")) {
            try (BufferedReader br = new BufferedReader(isr)) {
                int b;
                buf = new StringBuilder(512);
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
            }
        }

        return buf.toString();
    }

    public Map<String, String> getBodyUrlEncoded() throws IOException {
        String body = getBody();

        return parseUrlEncodedData(body);
    }

    public Map<String, String> getQuery() throws IOException {
        String query = this.exchange.getRequestURI().getQuery();

        return parseUrlEncodedData(query);
    }

    private Map<String, String> parseUrlEncodedData(String urlEncodedData) throws IOException {
        Map<String, String> properties = new LinkedHashMap<>();
        if (urlEncodedData == null) {
            return properties;
        }
        String[] pairs = urlEncodedData.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            properties.put(
                    URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
            );
        }

        return properties;
    }

}
