package api.vanilla.route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import api.vanilla.stream.Request;
import api.vanilla.stream.Response;

public class RouteResolver implements HttpHandler {

    private static RouteResolver instance;
    private final List<Route> routes;

    public static RouteResolver getInstance() {
        if (RouteResolver.instance == null) {
            RouteResolver.instance = new RouteResolver();
        }

        return RouteResolver.instance;
    }

    private RouteResolver() {
        this.routes = new ArrayList<>();
    }

    public void register(Router router) {
        this.routes.addAll(router.getRoutes());
    }

    public void register(Route route) {
        this.routes.add(route);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            for (Route route : this.routes) {
                System.out.println(exchange.getRequestMethod() + " " + route.getMethod());
                if (exchange.getRequestMethod().equals(route.getMethod())) {
                    System.out.println(exchange.getRequestURI().getPath() + " " + route.getUrl());
                    if (exchange.getRequestURI().getPath().equals(route.getUrl())) {
                        Request request = new Request(exchange);
                        Response response = new Response(exchange);
                        route.getCallback().resolve(request, response);
                        return;
                    }
                }
            }
            String message = "URL not found";
            exchange.sendResponseHeaders(404, message.length());
            exchange.getResponseBody().write(message.getBytes());
        } catch (IOException e) {
            System.err.println(e);
            exchange.sendResponseHeaders(500, e.getMessage().length());
            exchange.getResponseBody().write(e.getMessage().getBytes());
        } finally {
            exchange.close();
        }
    }

}
