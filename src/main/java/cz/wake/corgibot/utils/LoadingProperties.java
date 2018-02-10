package cz.wake.corgibot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class LoadingProperties {

    //TODO: JSON

    private String botToken, imgFlipToken;
    private String host, port, dbname, dbuser, dbpassword, minConnections, maxConnections, timeout, mashapeGameKey;
    private String beta;

    public LoadingProperties() {
        try {
            File configFile = new File("config.yml");

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
            imgFlipToken = properties.getProperty("imgflipToken");
            mashapeGameKey = properties.getProperty("mashapeGameKey");
            beta = properties.getProperty("beta");

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

    public String getImgFlipToken() {
        return imgFlipToken;
    }

    public String getMashapeGameKey() {
        return mashapeGameKey;
    }

    public boolean isBeta() {
        return beta.equalsIgnoreCase("true");
    }
}
