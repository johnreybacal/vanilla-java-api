package api.vanilla.handlers;

import java.util.UUID;

public class User {

    private UUID id;
    private String name;
    private String email;

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\":" + "\"" + this.id.toString() + "\","
                + "\"name\":" + "\"" + this.name + "\","
                + "\"email\":" + "\"" + this.email + "\""
                + "}";
    }
}
