package api.vanilla.route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import api.vanilla.exception.ServerException;
import api.vanilla.stream.Request;
import api.vanilla.stream.Response;

/**
 * Iterates through all routes and resolves the callback of a matching route
 */
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

    /**
     * Register a router
     *
     * @param router
     */
    public void register(Router router) {
        this.routes.addAll(router.getRoutes());
    }

    /**
     * Register a route
     *
     * @param route
     */
    public void register(Route route) {
        this.routes.add(route);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            for (Route route : this.routes) {
                if (RouteHelper.isRouteMatch(route, exchange.getRequestMethod(), exchange.getRequestURI().getPath())) {
                    Request request = new Request(exchange);
                    Response response = new Response(exchange);
                    RouteHelper.setRequestParameters(request, route.getParameters());

                    RequestResolver[] callbacks = route.getCallback();
                    for (int i = 0; i < callbacks.length; i++) {
                        callbacks[i].resolve(request, response);
                    }

                    return;
                }
            }
            throw ServerException.notFound();
        } catch (ServerException e) {
            exchange.sendResponseHeaders(e.getStatusCode(), e.getMessage().length());
            exchange.getResponseBody().write(e.getMessage().getBytes());
        } finally {
            exchange.close();
        }
    }

}
