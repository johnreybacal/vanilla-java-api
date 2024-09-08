package api.dev.user;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class User {

    private UUID id;
    private String name;
    private String email;

    public void setId(UUID id) {
        this.id = id;
    }

    public void setId(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        this.setId(new UUID(high, low));
    }

    public UUID getId() {
        return id;
    }

    public byte[] getIdBytes() {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(this.id.getMostSignificantBits());
        bb.putLong(this.id.getLeastSignificantBits());
        return bb.array();
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

    public static User fromMap(Map<String, String> map) {
        User user = new User();

        user.name = map.get("name");
        user.email = map.get("email");

        return user;
    }

    public static User fromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();

        user.setId(resultSet.getBytes("id"));
        user.setEmail(resultSet.getString("email"));
        user.setName(resultSet.getString("name"));

        return user;
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
