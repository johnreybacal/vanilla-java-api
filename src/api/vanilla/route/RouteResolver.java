package api.vanilla.route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import api.vanilla.exception.ServerException;
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
        try (exchange) {
            for (Route route : this.routes) {
                if (RouteHelper.isRouteMatch(route, exchange.getRequestMethod(), exchange.getRequestURI().getPath())) {
                    Request request = new Request(exchange);
                    Response response = new Response(exchange);
                    RouteHelper.setRequestParameters(request, route.getParameters());

                    route.getCallback().resolve(request, response);

                    return;
                }
            }
            throw ServerException.notFound();
        } catch (ServerException e) {
            exchange.sendResponseHeaders(e.getStatusCode(), e.getMessage().length());
            exchange.getResponseBody().write(e.getMessage().getBytes());
        }
    }

}
