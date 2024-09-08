package api.vanilla.route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import api.vanilla.stream.Request;

public class RouteHelper {

    public static List<String> getSegments(String path) {
        List<String> segments = Stream.of(path.split("/"))
                .filter(predicate -> predicate.length() > 0)
                .collect(Collectors.toList());
        return segments;
    }

    public static Map<Integer, String> getParameters(List<String> urlSegments) {
        Map<Integer, String> parameters = new HashMap<>();

        for (int i = 0; i < urlSegments.size(); i++) {
            String segment = urlSegments.get(i);
            if (segment.startsWith(":")) {
                parameters.put(i, segment.substring(1));
            }
        }

        return parameters;
    }

    public static boolean isRouteMatch(Route route, String method, String url) {
        if (!route.getMethod().equals(method)) {
            return false;
        }

        List<String> target = getSegments(route.getUrl());
        List<String> current = getSegments(url);

        if (target.size() != current.size()) {
            return false;
        }

        Set<Integer> keys = route.getParameters().keySet();

        for (int key : keys) {
            target.remove(key);
            current.remove(key);
        }

        return String.join("", target)
                .equals(String.join("", current));
    }

    public static void setRequestParameters(Request request, Map<Integer, String> routeParams) {
        List<String> urlSegments = getSegments(request.getPath());

        Set<Integer> keys = routeParams.keySet();
        Map<String, String> requestParams = request.getParameters();

        for (int key : keys) {
            requestParams.put(routeParams.get(key), urlSegments.get(key));
        }
    }
}
