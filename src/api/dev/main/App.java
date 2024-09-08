package api.dev.main;

import java.io.IOException;

import com.sun.net.httpserver.HttpServer;

import api.dev.user.UserController;
import api.vanilla.route.Method;
import api.vanilla.route.RouteResolver;
import api.vanilla.route.Router;

public class App {

    public static void main(String[] args) throws IOException {
        HttpServer server = ServerFactory.create();
        UserController userController = new UserController();
        Router router = new Router("/users");
        router.on(Method.GET, "/", userController::list);

        RouteResolver.getInstance().register(router);
        server.start();
    }
}
