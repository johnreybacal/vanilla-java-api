package api.vanilla.route;

import api.vanilla.exception.ServerException;
import api.vanilla.stream.Request;
import api.vanilla.stream.Response;

public interface RequestResolver {
    /**
     * Callback for resolving requests
     * @param request
     * @param response
     * @throws ServerException
     */
    public void resolve(Request request, Response response) throws ServerException;
}
