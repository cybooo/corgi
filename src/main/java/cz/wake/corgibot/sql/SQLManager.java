package cz.wake.corgibot.sql;

import com.zaxxer.hikari.HikariDataSource;
import cz.wake.corgibot.CorgiBot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class SQLManager {

    private final CorgiBot plugin;
    private final ConnectionPoolManager pool;
    private HikariDataSource dataSource;

    public SQLManager(CorgiBot plugin) {
        this.plugin = plugin;
        pool = new ConnectionPoolManager(plugin);
    }

    public void onDisable() {
        pool.closePool();
    }

    public ConnectionPoolManager getPool() {
        return pool;
    }

    public final int getPlayerBalance(final String nick, final String type) { //balance (CC), skykeys (SK)
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT " + type + " FROM CraftCoins WHERE nick = '" + nick + "';");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getInt(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 0;
    }

    public final int getPlayerVotes(final String nick, final String type) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT " + type + " FROM votes WHERE last_name = '" + nick + "';");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getInt(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 0;
    }

    public final Long getPlayerNextVote(String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT last_vote FROM votes WHERE last_name = '" + p + "'");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getLong("last_vote");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return (long) 0;
    }
}
