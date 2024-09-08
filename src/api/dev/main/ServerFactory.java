package api.dev.main;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import api.vanilla.route.RouteResolver;

public class ServerFactory {

    public static HttpServer create() throws IOException {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        RouteResolver routeResolver = RouteResolver.getInstance();
        server.createContext("/", routeResolver);
        server.setExecutor(null); // creates a default executor

        return server;
    }
}
