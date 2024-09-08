package api.vanilla.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

public class Response {

    private final HttpExchange exchange;

    public Response(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public Response setContentType(String[] contentType) {
        exchange.getResponseHeaders().put("Content-Type", List.of(contentType));

        return this;
    }

    public void send(int statusCode, Object data) throws IOException {
        System.out.println(exchange.getRequestMethod() + " "
                + exchange.getRequestURI().getPath() + " "
                + statusCode);
        String response = data.toString();

        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
