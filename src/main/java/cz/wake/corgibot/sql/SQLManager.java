package cz.wake.corgibot.sql;

import com.zaxxer.hikari.HikariDataSource;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.objects.ChangeLog;
import cz.wake.corgibot.objects.GiveawayObject;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.objects.TemporaryReminder;
import cz.wake.corgibot.objects.user.UserGuildData;
import cz.wake.corgibot.objects.user.UserWrapper;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class SQLManager {

    private final CorgiBot plugin;
    private final ConnectionPoolManager pool;
    private HikariDataSource dataSource;

    public SQLManager(CorgiBot plugin) {
        this.plugin = plugin;
        pool = new ConnectionPoolManager(plugin, "MySQL-Pool");
    }

    public void onDisable() {
        pool.closePool();
    }

    public ConnectionPoolManager getPool() {
        return pool;
    }

    public final void updatePrefix(final String guildId, final String prefix) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE s3_corgi.guild_data SET prefix = ? WHERE guild_id = ?;");
            ps.setString(1, prefix);
            ps.setString(2, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void addIgnoredChannel(final String guildId, final String channelId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO s3_corgi.ignored_channels (guild_id, channel_id) VALUES (?, ?);");
            ps.setString(1, guildId);
            ps.setString(2, channelId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void deleteIgnoredChannel(final String channelId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM s3_corgi.ignored_channels WHERE channel_id = ?");
            ps.setString(1, channelId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void deleteAllIgnoredChannels(final String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM s3_corgi.ignored_channels WHERE guild_id = ?");
            ps.setString(1, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final HashSet<MessageChannel> getIgnoredChannels(final String guildId) {
        HashSet<MessageChannel> list = new HashSet<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT channel_id FROM s3_corgi.ignored_channels WHERE guild_id = ?;");
            ps.setString(1, guildId);
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                try {
                    MessageChannel tx = CorgiBot.getJda().getGuildById(guildId).getTextChannelById(ps.getResultSet().getString("channel_id"));
                    if (tx != null) {
                        list.add(tx);
                    }
                    //TODO: Delete the ignored channel from SQL if it's deleted.
                } catch (NullPointerException e) {
                    CorgiBot.LOGGER.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return list;
    }

    public final void addReminder(final String userId, final long remindTime, final String message) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SET NAMES utf8mb4;INSERT INTO s3_corgi.reminders (user_id, remind_time, reminder) VALUES (?, ?, ?);");
            ps.setString(1, userId);
            ps.setLong(2, remindTime);
            ps.setString(3, message);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void deleteReminder(final String userId, final long remindTime) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM s3_corgi.reminders WHERE user_id = ? AND remind_time = ?");
            ps.setString(1, userId);
            ps.setLong(2, remindTime);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void deleteReminderById(final String userId, final int reminderId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM s3_corgi.reminders WHERE user_id = ? AND id = ?");
            ps.setString(1, userId);
            ps.setInt(2, reminderId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void registerUser(String userId, String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO s3_corgi.user_data (user_id, guild_id, level, xp, voice_time, messages) VALUES (?, ?, ?, ?, ?, ?);");
            ps.setString(1, userId);
            ps.setString(2, guildId);
            ps.setInt(3, 1);
            ps.setInt(4, 0);
            ps.setInt(5, 0);
            ps.setInt(6, 0);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final boolean hasData(String userId, String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM s3_corgi.user_data WHERE user_id = '" + userId + "' AND guild_id = '" + guildId + "';");
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final boolean existsGuildData(final String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM s3_corgi.guild_data WHERE guild_id = '" + guildId + "';");
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void insertDefaultServerData(final String guildId, final String prefix) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO s3_corgi.guild_data (guild_id, prefix) VALUES (?,?);");
            ps.setString(1, guildId);
            ps.setString(2, prefix);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void deleteOldData(final String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM s3_corgi.prefixes WHERE guild_id = ?");
            ps.setString(1, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public HashSet<TemporaryReminder> getRemindersByUser(final String userId) {
        HashSet<TemporaryReminder> list = new HashSet<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM s3_corgi.reminders WHERE user_id = ?;");
            ps.setString(1, userId);
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                list.add(new TemporaryReminder(ps.getResultSet().getInt("id"), ps.getResultSet().getString("user_id"), ps.getResultSet().getLong("remind_time"), ps.getResultSet().getString("reminder")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return list;
    }

    public HashSet<TemporaryReminder> getAllReminders() {
        HashSet<TemporaryReminder> list = new HashSet<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM s3_corgi.reminders;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                list.add(new TemporaryReminder(ps.getResultSet().getInt("id"), ps.getResultSet().getString("user_id"), ps.getResultSet().getLong("remind_time"), ps.getResultSet().getString("reminder")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return list;
    }

    public GuildWrapper createGuildWrapper(String id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM s3_corgi.guild_data WHERE guild_id = ?;");
            ps.setString(1, id);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return new GuildWrapper(id).setPrefix(ps.getResultSet().getString("prefix"), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public UserWrapper createUserWrapper(String id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        UserWrapper userWrapper = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM s3_corgi.user_data WHERE user_id = ?;");
            ps.setString(1, id);
            ps.executeQuery();
            rs = ps.getResultSet();
            while (rs.next()) {
                if (userWrapper == null) {
                    userWrapper = new UserWrapper(id);
                }
                userWrapper.getGuildData().put(rs.getString("guild_id"), new UserGuildData(id, rs.getString("guild_id")));
            }
            return userWrapper;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }
        return null;
    }

    public final ChangeLog getLastChanges() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM s3_corgi.changelog;");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return new ChangeLog(ps.getResultSet().getLong("date"),
                        ps.getResultSet().getString("news"),
                        ps.getResultSet().getString("fixes"),
                        ps.getResultSet().getString("warning"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public final void registerGiveawayInSQL(final String guildId, final String textChannelId, final String messageId, final long startTime, final long endTime,
                                            final String prize, final int maxWinners, final String emojiCode, final String embedColor) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SET NAMES utf8mb4;INSERT INTO s3_corgi.giveaways (guild_id, textchannel_id, message_id, start_time, end_time, prize, max_winners, emoji, embed_color) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
            ps.setString(1, guildId);
            ps.setString(2, textChannelId);
            ps.setString(3, messageId);
            ps.setLong(4, startTime);
            ps.setLong(5, endTime);
            ps.setString(6, prize);
            ps.setInt(7, maxWinners);
            ps.setString(8, emojiCode);
            ps.setString(9, embedColor);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void deleteGiveawayFromSQL(final String guildId, final String messageId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM s3_corgi.giveaways WHERE message_id = ?");
            ps.setString(1, messageId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public HashSet<GiveawayObject> getAllGiveaways() {
        HashSet<GiveawayObject> list = new HashSet<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM s3_corgi.giveaways;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                list.add(new GiveawayObject(ps.getResultSet().getInt("id"),
                        ps.getResultSet().getString("guild_id"),
                        ps.getResultSet().getString("textchannel_id"),
                        ps.getResultSet().getString("message_id"),
                        ps.getResultSet().getLong("end_time"),
                        ps.getResultSet().getString("prize"),
                        ps.getResultSet().getInt("max_winners"),
                        ps.getResultSet().getString("emoji"),
                        ps.getResultSet().getString("embed_color")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return list;
    }

    public String getRandomFact() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT fact FROM s3_corgi.fakty ORDER BY RAND() LIMIT 1;");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("fact");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public final void updateLanguage(final String guildId, final String language) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE s3_corgi.guild_data SET language = ? WHERE guild_id = ?;");
            ps.setString(1, language);
            ps.setString(2, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void addRoleMusicCommand(final String guildId, final String roleId, final String commandName) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO s3_corgi.music_permission_data (guild_id, role_id, command_name) VALUES (?, ?, ?);");
            ps.setString(1, guildId);
            ps.setString(2, roleId);
            ps.setString(3, commandName);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void deleteRoleMusicCommand(final String guildId, final String roleId, final String commandName) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM s3_corgi.music_permission_data WHERE guild_id = ? AND role_id = ? AND command_name = ?;");
            ps.setString(1, guildId);
            ps.setString(2, roleId);
            ps.setString(3, commandName);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final ArrayList<String> getRoleMusicCommands(final String guildId, final String roleId) {
        ArrayList<String> commands = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT command_name FROM s3_corgi.music_permission_data WHERE guild_id = ? AND role_id = ?;");
            ps.setString(1, guildId);
            ps.setString(2, roleId);
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                try {
                    commands.add(ps.getResultSet().getString("command_name"));
                } catch (NullPointerException e) {
                    CorgiBot.LOGGER.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return commands;
    }

    public final ArrayList<String> getRoleMusicRoles(final String guildId, final String commandName) {
        ArrayList<String> commands = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT role_id FROM s3_corgi.music_permission_data WHERE guild_id = ? AND command_name = ?;");
            ps.setString(1, guildId);
            ps.setString(2, commandName);
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                try {
                    commands.add(ps.getResultSet().getString("role_id"));
                } catch (NullPointerException e) {
                    CorgiBot.LOGGER.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return commands;
    }

    public final boolean registeredTicketData(String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM s3_corgi.ticket_guild_data WHERE guild_id = ?;");
            ps.setString(1, guildId);
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void registerTicketData(String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO s3_corgi.ticket_guild_data (guild_id, opened_tickets, ticket_closed_category, ticket_transcript_channel, ticket_opened_category, maximum_tickets, maximum_user_tickets) VALUES (?, ?, ?, ?, ?, ?, ?);");
            ps.setString(1, guildId);
            ps.setInt(2, 0);
            ps.setString(3, "0");
            ps.setString(4, "0");
            ps.setString(5, "0");
            ps.setString(6, "10");
            ps.setString(7, "5");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final String getTicketOpenedCategory(String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT ticket_opened_category FROM s3_corgi.ticket_guild_data WHERE guild_id = ?;");
            ps.setString(1, guildId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("ticket_opened_category");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public final void setTicketOpenedCategory(String guildId, String categoryId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE s3_corgi.ticket_guild_data SET ticket_opened_category = ? WHERE guild_id = ?;");
            ps.setString(1, categoryId);
            ps.setString(2, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final String getTicketClosedCategory(String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT ticket_closed_category FROM s3_corgi.ticket_guild_data WHERE guild_id = ?;");
            ps.setString(1, guildId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("ticket_closed_category");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public final void setTicketClosedCategory(String guildId, String categoryId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE s3_corgi.ticket_guild_data SET ticket_closed_category = ? WHERE guild_id = ?;");
            ps.setString(1, categoryId);
            ps.setString(2, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final String getTicketTranscriptChannel(String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT ticket_transcript_channel FROM s3_corgi.ticket_guild_data WHERE guild_id = ?;");
            ps.setString(1, guildId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("ticket_transcript_channel");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public final void setTicketTranscriptChannel(String guildId, String channelId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE s3_corgi.ticket_guild_data SET ticket_transcript_channel = ? WHERE guild_id = ?;");
            ps.setString(1, channelId);
            ps.setString(2, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final int getOpenedTickets(String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT opened_tickets FROM s3_corgi.ticket_guild_data WHERE guild_id = ?;");
            ps.setString(1, guildId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getInt("opened_tickets");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return new Random().nextInt(9999); // If anything goes wrong, try to assign a random id to the ticket.
    }

    public final void setOpenedTickets(String guildId, int openedTickets) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE s3_corgi.ticket_guild_data SET opened_tickets = ? WHERE guild_id = ?");
            ps.setInt(1, openedTickets);
            ps.setString(2, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final int getMaximumTickets(String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT maximum_tickets FROM s3_corgi.ticket_guild_data WHERE guild_id = ?;");
            ps.setString(1, guildId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getInt("maximum_tickets");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 10;
    }

    public final void setMaximumTickets(String guildId, int maximumTickets) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE s3_corgi.ticket_guild_data SET maximum_tickets = ? WHERE guild_id = ?");
            ps.setInt(1, maximumTickets);
            ps.setString(2, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final int getMaximumUserTickets(String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT maximum_user_tickets FROM s3_corgi.ticket_guild_data WHERE guild_id = ?;");
            ps.setString(1, guildId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getInt("maximum_user_tickets");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 2;
    }

    public final void setMaximumUserTickets(String guildId, int maximumUserTickets) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE s3_corgi.ticket_guild_data SET maximum_user_tickets = ? WHERE guild_id = ?");
            ps.setInt(1, maximumUserTickets);
            ps.setString(2, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final HashSet<Role> getTicketStaffRoles(String guildId) {
        HashSet<Role> list = new HashSet<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT role_id FROM s3_corgi.ticket_guild_data_staffroles WHERE guild_id = ?;");
            ps.setString(1, guildId);
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                try {
                    Role role = CorgiBot.getJda().getGuildById(guildId).getRoleById(ps.getResultSet().getString("roleid"));
                    if (role != null) {
                        list.add(role);
                    }
                } catch (NullPointerException e) {
                    CorgiBot.LOGGER.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return list;
    }

    public final void addTicketStaffRole(String guildId, String roleId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO s3_corgi.ticket_guild_data_staffroles (guild_id, role_id) VALUES (?, ?);");
            ps.setString(1, guildId);
            ps.setString(2, roleId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void deleteTicketStaffRole(String guildId, String roleId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM s3_corgi.ticket_guild_data_staffroles WHERE guild_id = ? AND role_id = ?;");
            ps.setString(1, guildId);
            ps.setString(2, roleId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public long getLevel(String userId, String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT level FROM s3_corgi.user_data WHERE user_id = ? AND guild_id = ?;");
            ps.setString(1, userId);
            ps.setString(2, guildId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getLong("level");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 0;
    }

    public void setLevel(String userId, String guildId, long level) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE s3_corgi.user_data SET level = ? WHERE user_id = ? AND guild_id = ?;");
            ps.setLong(1, level);
            ps.setString(2, userId);
            ps.setString(3, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public long getXp(String userId, String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT xp FROM s3_corgi.user_data WHERE user_id = ? AND guild_id = ?;");
            ps.setString(1, userId);
            ps.setString(2, guildId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getLong("xp");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 0;
    }

    public void setXp(String userId, String guildId, long xp) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE s3_corgi.user_data SET xp = ? WHERE user_id = ? AND guild_id = ?;");
            ps.setLong(1, xp);
            ps.setString(2, userId);
            ps.setString(3, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public long getVoiceTime(String userId, String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT voice_time FROM s3_corgi.user_data WHERE user_id = ? AND guild_id = ?;");
            ps.setString(1, userId);
            ps.setString(2, guildId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getLong("voice_time");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 0;
    }

    public void setVoiceTime(String userId, String guildId, long voiceTime) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE s3_corgi.user_data SET voice_time = ? WHERE user_id = ? AND guild_id = ?;");
            ps.setLong(1, voiceTime);
            ps.setString(2, userId);
            ps.setString(3, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public long getMessages(String userId, String guildId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT messages FROM s3_corgi.user_data WHERE user_id = ? AND guild_id = ?;");
            ps.setString(1, userId);
            ps.setString(2, guildId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getLong("messages");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 0;
    }

    public void setMessages(String userId, String guildId, long messages) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE s3_corgi.user_data SET messages = ? WHERE user_id = ? AND guild_id = ?;");
            ps.setLong(1, messages);
            ps.setString(2, userId);
            ps.setString(3, guildId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

}
