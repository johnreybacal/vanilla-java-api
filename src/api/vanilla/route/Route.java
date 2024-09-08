package api.vanilla.route;

public class Route {

    private final String url;
    private final Method method;
    private final RequestResolver callback;

    public Route(String url,
            Method method,
            RequestResolver callback
    ) {
        this.url = url;
        this.method = method;
        this.callback = callback;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method.toString();
    }

    public RequestResolver getCallback() {
        return callback;
    }
}
