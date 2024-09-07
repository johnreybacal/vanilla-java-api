package api.vanilla.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Driver {

    private static Driver instance;
    private Connection connection;

    private Driver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String pass = System.getenv("DB_PASS");
            this.connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Driver getInstance() {
        if (Driver.instance == null) {
            Driver.instance = new Driver();
        }
        return Driver.instance;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
