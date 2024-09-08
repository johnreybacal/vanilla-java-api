package api.vanilla.main;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import api.vanilla.handlers.UserHandler;

public class App {

    public static void main(String[] args) throws Exception {
        // reference: https://stackoverflow.com/a/3732328
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/users", new UserHandler());
        HttpContext context = server.createContext("/he");
        server.setExecutor(null); // creates a default executor
        server.start();

        System.out.println("Server listening to port: " + port);
    }

}
