package api.vanilla.route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import api.vanilla.stream.Request;

public class RouteHelper {

    /**
     * Get the segments of the path, omitting empty segments
     * @param path
     * @return List of segments
     */
    public static List<String> getSegments(String path) {
        List<String> segments = Stream.of(path.split("/"))
                .filter(predicate -> predicate.length() > 0)
                .collect(Collectors.toList());
        return segments;
    }

    /**
     * Detects and maps the parameters in a URL segment
     * @param urlSegments
     * @return Map of parameters - (key: index, value: parameter)
     */
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

    /**
     * Check if the request Method and URL matches a Route
     * @param route
     * @param method
     * @param url
     * @return boolean
     */
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

    /**
     * Compares the request URL against the route paramaters and attaches it to the request
     * @param request
     * @param routeParams
     */
    public static void setRequestParameters(Request request, Map<Integer, String> routeParams) {
        List<String> urlSegments = getSegments(request.getPath());

        Set<Integer> keys = routeParams.keySet();
        Map<String, String> requestParams = request.getParameters();

        for (int key : keys) {
            requestParams.put(routeParams.get(key), urlSegments.get(key));
        }
    }
}
