package api.vanilla.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import api.vanilla.database.Driver;

public class UserHandler implements HttpHandler {

    Connection connection;

    public UserHandler() {
        this.connection = Driver.getInstance().getConnection();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String response = new String();
            String[] segments = exchange.getRequestURI().getPath().split("/");
            int statusCode = 200;

            if (method.equals("GET")) {
                if (segments.length == 2) {
                    Map<String, String> query = parseQuery(exchange.getRequestURI());
                    response = this.get(query);

                } else if (segments.length == 3) {
                    String id = segments[2];
                    response = this.getOne(id);
                    if (response == null) {
                        statusCode = 404;
                        response = "User not found";
                    }
                }
            }
            exchange.sendResponseHeaders(statusCode, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException | SQLException e) {

            System.out.println(e);
            exchange.sendResponseHeaders(500, e.getMessage().length());
            exchange.getResponseBody().write(e.getMessage().getBytes());
        } catch (Exception e) {
            System.out.println("Exception");
            System.out.println(e);
            exchange.sendResponseHeaders(500, e.getMessage().length());
            exchange.getResponseBody().write(e.getMessage().getBytes());
        }
    }

    public String get(Map<String, String> query) throws SQLException {
        ResultSet resultSet;
        List<Map<String, String>> users = new ArrayList<>();
        try (Statement statement = this.connection.createStatement()) {
            resultSet = statement.executeQuery("SELECT * FROM user");
            while (resultSet.next()) {
                users.add(parseUser(resultSet));
            }
            resultSet.close();
        }

        return users.toString();
    }

    public String getOne(String id) throws SQLException {
        ResultSet resultSet;

        try (Statement statement = this.connection.createStatement()) {
            // Prone to SQL injection
            String query = "SELECT * FROM user WHERE id = uuid_to_bin('" + id + "')";
            System.out.println(query);
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return parseUser(resultSet).toString();
            } else {
                return null;
            }
        }

    }

    public Map<String, String> parseQuery(URI uri) throws UnsupportedEncodingException {
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

    public Map<String, String> parseUser(ResultSet resultSet) throws SQLException {
        Map<String, String> user = new HashMap<>();

        user.put("id", decryptUUID(resultSet.getBytes("id")).toString());
        user.put("email", resultSet.getString("email"));
        user.put("name", resultSet.getString("name"));

        return user;
    }

    public UUID decryptUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();

        return new UUID(high, low);
    }

}
