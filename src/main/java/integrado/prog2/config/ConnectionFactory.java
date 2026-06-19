package integrado.prog2.config;

import integrado.prog2.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private final String url;
    private final String username;
    private final String password;

    public ConnectionFactory() {
        PersistenceConfig config = PersistenceConfig.load();
        String driver = config.getRequiredProperty("db.driver");
        this.url = config.getRequiredProperty("db.url");
        this.username = config.getRequiredProperty("db.username");
        this.password = config.getRequiredProperty("db.password");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException exception) {
            throw new DataAccessException("Could not load JDBC driver.", exception);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
