package api.dev.user;

import java.util.List;
import java.util.UUID;

import api.vanilla.stream.Request;
import api.vanilla.stream.Response;

public class UserController {

    UserService service;

    public UserController() {
        this.service = new UserService();
    }

    public void list(Request request, Response response) {
        try {
            List<User> users = service.list();

            response.sendJson(200, users);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void create(Request request, Response response) {
        try {
            User user = User.fromMap(request.getBodyUrlEncoded());
            user.setId(UUID.randomUUID());

            user = service.create(user);

            response.sendJson(201, user);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
