package api.vanilla.route;

import api.vanilla.stream.Request;
import api.vanilla.stream.Response;

public interface RequestResolver {

    public void resolve(Request request, Response response);
}
