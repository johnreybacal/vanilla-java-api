package api.vanilla.main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class App {

    public static void main(String[] args) throws Exception {
        // reference: https://stackoverflow.com/a/3732328
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/hello", (HttpExchange httpExchange) -> {
            try {
                String method = httpExchange.getRequestMethod();
                String response = new String();
                int statusCode = 200;
                System.out.println(method);

                if (method.equals("GET")) {
                    response = "Hello";

                    Map<String, String> queries = parseQuery(httpExchange.getRequestURI());
                    String name = queries.get("name");

                    if (name != null) {
                        response += ", " + name + "!";
                    }
                    System.out.println(response);
                }

                httpExchange.sendResponseHeaders(statusCode, response.length());
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (IOException e) {
                System.out.println("IOException");
                System.out.println(e);
                httpExchange.sendResponseHeaders(500, e.getMessage().length());
                httpExchange.getResponseBody().write(e.getMessage().getBytes());
            } catch (Exception e) {
                System.out.println("Exception");
                System.out.println(e);
                httpExchange.sendResponseHeaders(500, e.getMessage().length());
                httpExchange.getResponseBody().write(e.getMessage().getBytes());
            }
        });
        server.setExecutor(null); // creates a default executor
        server.start();

        System.out.println("Server listening to port: " + port);
    }

    public static Map<String, String> parseQuery(URI uri) throws UnsupportedEncodingException {
        // reference: https://stackoverflow.com/a/13592567
        Map<String, String> queries = new LinkedHashMap<>();
        String query = uri.getQuery();
        if (query == null) {
            return queries;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            queries.put(
                    URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
            );
        }
        return queries;
    }

}
