package api.vanilla.route;

import java.util.ArrayList;
import java.util.List;

public class Router {

    private final String url;
    private final List<Route> routes;

    public Router(String url) {
        this.url = url;
        routes = new ArrayList<>();
    }

    public void on(Method method, String url, RequestResolver callback) {
        url = this.url + url;
        Route route = new Route(url, method, callback);
        this.routes.add(route);
    }

    public String getUrl() {
        return url;
    }

    public List<Route> getRoutes() {
        return routes;
    }
}
