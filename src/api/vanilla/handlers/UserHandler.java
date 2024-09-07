package api.vanilla.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
            } else if (method.equals("POST")) {
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

                Map<String, String> body = parseUrlEncoded(buf.toString());
                response = post(body);
            }

            String[] contentType = {"application/json"};
            exchange.getResponseHeaders().put("Content-Type", List.of(contentType));
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
        List<User> users = new ArrayList<>();
        try (Statement statement = this.connection.createStatement()) {
            resultSet = statement.executeQuery("SELECT * FROM user");
            while (resultSet.next()) {
                users.add(User.fromResultSet(resultSet));
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
                return User.fromResultSet(resultSet).toString();
            } else {
                return null;
            }
        }
    }

    public String post(Map<String, String> body) throws SQLException {
        User user = User.fromMap(body);

        String query = "INSERT INTO user (id, name, email) VALUES (?, ?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            user.setId(UUID.randomUUID());
            statement.setBytes(1, user.getIdBytes());
            statement.setString(2, user.getName());
            statement.setString(3, user.getEmail());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            // Existing table does not have default value for id, must be the JPA config in spring project
            // try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            //     if (generatedKeys.next()) {
            //         user.setId(generatedKeys.getBytes(1));
            //     } else {
            //         throw new SQLException("Creating user failed, no ID obtained.");
            //     }
            // }
            return user.toString();
        }
    }

    public Map<String, String> parseQuery(URI uri) throws UnsupportedEncodingException {
        // reference: https://stackoverflow.com/a/13592567
        String query = uri.getQuery();
        if (query == null) {
            return new LinkedHashMap<>();
        }

        return parseUrlEncoded(query);
    }

    public Map<String, String> parseUrlEncoded(String encodedUrl) throws UnsupportedEncodingException {
        Map<String, String> properties = new LinkedHashMap<>();
        if (encodedUrl == null) {
            return properties;
        }
        String[] pairs = encodedUrl.split("&");
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
