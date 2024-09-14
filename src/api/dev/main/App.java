package api.dev.main;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import api.dev.user.UserController;
import api.vanilla.route.Method;
import api.vanilla.route.RequestResolver;
import api.vanilla.route.RouteResolver;
import api.vanilla.route.Router;
import api.vanilla.stream.Request;
import api.vanilla.stream.Response;

public class App {

    public static void main(String[] args) throws IOException {
        HttpServer server = ServerFactory.create();
        UserController userController = new UserController();
        Router router = new Router("/users");

        RequestResolver log = (Request request, Response response) -> {
            HttpExchange exchange = response.getExchange();
            System.out.println(exchange.getRequestMethod() + " "
                    + exchange.getRequestURI().getPath() + " "
                    + exchange.getResponseCode() + " - "
                    + exchange.getResponseHeaders().getFirst("Content-length"));
        };

        router.on(Method.GET, userController::list, log);
        router.on(Method.GET, "/:id", userController::get, log);
        router.on(Method.POST, userController::create, log);
        router.on(Method.PUT, "/:id", userController::update, log);
        router.on(Method.DELETE, "/:id", userController::delete, log);

        RouteResolver.getInstance().register(router);
        server.start();
    }
}
