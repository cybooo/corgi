package cz.wake.corgibot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class LoadingProperties {

    private String botToken;
    private String host, port, dbname, dbuser, dbpassword, minConnections, maxConnections, timeout;

    public LoadingProperties() {
        try {
            File configFile = new File("pre-config.yml");

            FileInputStream fileInput = new FileInputStream(configFile);
            Properties properties = new Properties();
            properties.load(fileInput);
            fileInput.close();

            botToken = properties.getProperty("token");
            host = properties.getProperty("hostname");
            port = properties.getProperty("port");
            dbname = properties.getProperty("database");
            dbuser = properties.getProperty("username");
            dbpassword = properties.getProperty("password");
            minConnections = properties.getProperty("minimumConnections");
            maxConnections = properties.getProperty("maximumConnections");
            timeout = properties.getProperty("timeout");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBotToken() {
        return botToken;
    }

    public String getDatabaseHost() {
        return host;
    }

    public String getDatabasePort() {
        return port;
    }

    public String getDatabaseUser() {
        return dbuser;
    }

    public String getDatabaseName() {
        return dbname;
    }

    public String getDatabasePassword() {
        return dbpassword;
    }

    public int getMinConnections() {
        return Integer.valueOf(minConnections);
    }

    public int getMaxConnections() {
        return Integer.valueOf(maxConnections);
    }

    public long getTimeout() {
        return (long) Integer.valueOf(timeout);
    }
}
