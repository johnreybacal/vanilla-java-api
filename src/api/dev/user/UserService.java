package api.dev.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import api.vanilla.database.Driver;

public class UserService {

    Connection connection;

    public UserService() {
        this.connection = Driver.getInstance().getConnection();
    }

    public List<User> list() {
        ResultSet resultSet;
        List<User> users = new ArrayList<>();
        try (Statement statement = this.connection.createStatement()) {
            resultSet = statement.executeQuery("SELECT * FROM user");
            while (resultSet.next()) {
                users.add(User.fromResultSet(resultSet));
            }
            resultSet.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return users;
    }

    public User get(UUID id) {
        String query = "SELECT * FROM user WHERE id = uuid_to_bin(?)";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, id.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return User.fromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    public User create(User user) {
        String query = "INSERT INTO user (id, name, email) VALUES (?, ?, ?)";

        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            user.setId(UUID.randomUUID());
            statement.setBytes(1, user.getIdBytes());
            statement.setString(2, user.getName());
            statement.setString(3, user.getEmail());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            return user;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public User update(User user) {
        String query = "UPDATE user SET name = ?, email = ? WHERE id = uuid_to_bin(?)";

        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getId().toString());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("User not found.");
            }

            return user;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public boolean delete(UUID id) {
        String query = "DELETE FROM user WHERE id = uuid_to_bin(?)";

        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, id.toString());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("User not found.");
            }

            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }
}
