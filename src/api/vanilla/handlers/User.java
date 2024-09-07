package api.vanilla.handlers;

import java.util.UUID;

public class User {

    UUID id;
    String name;
    String email;

    @Override
    public String toString() {
        return "{"
                + "\"id\":" + "\"" + this.id.toString() + "\","
                + "\"name\":" + "\"" + this.name + "\","
                + "\"email\":" + "\"" + this.email + "\""
                + "}";
    }
}
