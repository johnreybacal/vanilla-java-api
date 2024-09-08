package api.dev.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import api.dev.database.Driver;
import api.vanilla.exception.ServerException;

public class UserService {

    Connection connection;

    public UserService() {
        this.connection = Driver.getInstance().getConnection();
    }

    public List<User> list() throws SQLException {
        ResultSet resultSet;
        List<User> users = new ArrayList<>();
        try (Statement statement = this.connection.createStatement()) {
            resultSet = statement.executeQuery("SELECT * FROM user");
            while (resultSet.next()) {
                users.add(User.fromResultSet(resultSet));
            }
            resultSet.close();
            return users;
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public User get(UUID id) throws SQLException {
        String query = "SELECT * FROM user WHERE id = uuid_to_bin(?)";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, id.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return User.fromResultSet(resultSet);
            }
            return null;
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public User create(User user) throws SQLException {
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
            throw ex;
        }
    }

    public User update(User user) throws SQLException, ServerException {
        String query = "UPDATE user SET name = ?, email = ? WHERE id = uuid_to_bin(?)";

        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getId().toString());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw ServerException.notFound();
            }

            return user;
        } catch (SQLException | ServerException ex) {
            throw ex;
        }
    }

    public boolean delete(UUID id) throws SQLException, ServerException {
        String query = "DELETE FROM user WHERE id = uuid_to_bin(?)";

        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, id.toString());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw ServerException.notFound();
            }

            return true;
        } catch (SQLException | ServerException ex) {
            throw ex;
        }
    }
}
