package cz.wake.corgibot.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.LoadingProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionPoolManager {

    private final CorgiBot plugin;
    private HikariDataSource dataSource;
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;

    public ConnectionPoolManager(CorgiBot plugin, String name) {
        this.plugin = plugin;
        init();
        setupPool(name);
    }

    private void init() {
        LoadingProperties config = new LoadingProperties();
        host = config.getDatabaseHost();
        port = config.getDatabasePort();
        database = config.getDatabaseName();
        username = config.getDatabaseUser();
        password = config.getDatabasePassword();
        minimumConnections = config.getMinConnections();
        maximumConnections = config.getMaxConnections();
        connectionTimeout = config.getTimeout();
    }

    private void setupPool(String name) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=UTF-8&allowMultiQueries=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName(name);
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) try {
            conn.close();
        } catch (SQLException ignored) {
        }
        if (ps != null) try {
            ps.close();
        } catch (SQLException ignored) {
        }
        if (res != null) try {
            res.close();
        } catch (SQLException ignored) {
        }
    }
}
