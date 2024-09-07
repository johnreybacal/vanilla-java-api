package api.vanilla.main;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class App {

    public static void main(String[] args) throws Exception {
        List<Map<String, String>> users = new ArrayList<>();

        Map<String, String> user = new HashMap<>();
        user.put("id", "1");
        user.put("name", "USER 1");
        users.add(user);

        user = new HashMap<>();
        user.put("id", "2");
        user.put("name", "USER 2");
        users.add(user);

        // reference: https://stackoverflow.com/a/3732328
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/users", (HttpExchange httpExchange) -> {
            try {
                String method = httpExchange.getRequestMethod();
                String response = new String();
                String[] segments = httpExchange.getRequestURI().getPath().split("/");
                int statusCode = 200;
                System.out.println(method + " " + segments.length);

                if (method.equals("GET")) {
                    if (segments.length == 2) {
                        Map<String, String> queries = parseQuery(httpExchange.getRequestURI());

                        String qName = queries.get("name");

                        if (qName != null) {
                            final String name = qName.toLowerCase();
                            List<Map<String, String>> result = users.stream()
                                    .filter(u -> {
                                        String uName = u.get("name").toLowerCase();

                                        return uName.contains(name) || name.contains(uName);
                                    })
                                    .collect(Collectors.toList());

                            response = result.toString();
                        } else {
                            response = users.toString();
                        }
                    } else if (segments.length == 3) {
                        String id = segments[2];
                        List<Map<String, String>> result = users.stream()
                                .filter(u -> u.get("id").equals(id))
                                .collect(Collectors.toList());

                        if (result.isEmpty()) {
                            statusCode = 404;
                            response = "User not found";
                        } else {
                            response = result.get(0).toString();
                        }
                    }
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
