package api.dev.user;

import java.util.List;
import java.util.UUID;

import api.vanilla.exception.ServerException;
import api.vanilla.stream.Request;
import api.vanilla.stream.Response;

public class UserController {

    UserService service;

    public UserController() {
        this.service = new UserService();
    }

    public void list(Request request, Response response)
            throws ServerException {
        try {
            List<User> users = service.list();

            response.sendJson(200, users);
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }

    public void get(Request request, Response response)
            throws ServerException {
        try {
            String id = request.getParameters().get("id");
            User user = service.get(UUID.fromString(id));

            if (user == null) {
                throw ServerException.notFound();
            }
            response.sendJson(200, user);
        } catch (ServerException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }

    public void create(Request request, Response response)
            throws ServerException {
        try {
            User user = User.fromMap(request.getBodyUrlEncoded());
            user.setId(UUID.randomUUID());

            user = service.create(user);

            response.sendJson(201, user);
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }

    public void update(Request request, Response response)
            throws ServerException {
        try {
            User user = User.fromMap(request.getBodyUrlEncoded());
            String id = request.getParameters().get("id");
            user.setId(UUID.fromString(id));

            user = service.update(user);

            response.sendJson(200, user);
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }

    public void delete(Request request, Response response)
            throws ServerException {
        try {
            String id = request.getParameters().get("id");

            service.delete(UUID.fromString(id));

            response.sendJson(200, "User has been deleted");
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }
}
