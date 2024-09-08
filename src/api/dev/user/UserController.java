package api.dev.user;

import java.util.List;

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

            response.send(200, users);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
