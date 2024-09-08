package api.vanilla.route;

import java.util.HashMap;
import java.util.Map;

public class Route {

    private final String url;
    private final Method method;
    private final Map<Integer, String> parameters;
    private final RequestResolver callback;

    public Route(String url,
            Method method,
            RequestResolver callback
    ) {
        this.url = url;
        this.method = method;
        this.parameters = new HashMap<>();
        this.callback = callback;
    }

    public Route(String url,
            Method method,
            Map<Integer, String> parameters,
            RequestResolver callback
    ) {
        this.url = url;
        this.method = method;
        this.parameters = parameters;
        this.callback = callback;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method.toString();
    }

    public Map<Integer, String> getParameters() {
        return parameters;
    }

    public RequestResolver getCallback() {
        return callback;
    }

}
