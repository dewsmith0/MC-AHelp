package io.github.dewsmith0.mcahelp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AhelpHistory {
    public static final int MESSAGES_PER_PAGE = 20;
    private static final MCAhelp plugin = MCAhelp.getPlugin(MCAhelp.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static void addLog(AhelpEntry entry) {
        plugin.log.info(entry.toString());
        try (Connection connection = plugin.getDatabase().getConnection()) {
            String sql = """
                    INSERT INTO ahelp_logs (
                        player_uuid, sender_uuid, message, is_staff, should_notify, bwoinked
                    ) VALUES (?, ?, ?, ?, ?, ?);
                    """;
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, entry.playerUuid.toString());
            statement.setString(2, entry.senderUuid.toString());
            statement.setString(3, entry.message.strip());
            statement.setBoolean(4, entry.isStaff);
            statement.setBoolean(5, entry.shouldNotify);
            statement.setBoolean(6, entry.bwoinked);
            statement.execute();
        } catch (SQLException e) {
            plugin.logError(String.format("Failed to add ahelp log for %s", entry.playerUuid), e);
        }
    }
    public static ArrayList<AhelpEntry> getLogs(OfflinePlayer player, int page) {
        try (Connection connection = plugin.getDatabase().getConnection()){
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM ahelp_logs
                    WHERE player_uuid = ?
                    ORDER BY message_date DESC
                    LIMIT 20
                    OFFSET ?;
                    """);
            statement.setString(1, player.getUniqueId().toString());
            statement.setInt(2, (page-1)*MESSAGES_PER_PAGE);
            ResultSet result = statement.executeQuery();
            ArrayList<AhelpEntry> output = new ArrayList<>();
            while(result.next()) {
                UUID playerUuid = player.getUniqueId();
                UUID senderUuid = UUID.fromString(result.getString("sender_uuid"));
                String message = result.getString("message");
                Date timestamp = result.getDate("message_date");
                boolean isStaff = result.getBoolean("is_staff");
                boolean shouldNotify = result.getBoolean("should_notify");
                boolean bwoinked = result.getBoolean("bwoinked");
                AhelpEntry entry = new AhelpEntry(playerUuid, senderUuid, message, timestamp, isStaff, shouldNotify, bwoinked);
                output.add(entry);
            }
            Collections.reverse(output);
            return output;
        } catch (SQLException e) {
            plugin.logError(String.format("Failed to get ahelp logs for %s", player.getUniqueId()), e);
            return null;
        }
    }
    public static ArrayList<Component> parseLogs(List<AhelpEntry> entries) {
        ArrayList<Component> output = new ArrayList<>();
        for (AhelpHistory.AhelpEntry entry : entries) {
            String senderName = plugin.getServer().getOfflinePlayer(entry.senderUuid()).getName();
            String formattedDate = dateFormat.format(entry.timestamp());
            if (senderName == null) {
                senderName = "<ERROR>";
            }
            Component formattedLog = mm.deserialize("<gray>[<date>] <silent></gray><sender><reset>: <message>",
                    Placeholder.unparsed("date", formattedDate),
                    Placeholder.unparsed("silent", entry.bwoinked() || !entry.isStaff? "" : "(S) "),
                    Placeholder.component("sender", Component.text(senderName, entry.isStaff() ? NamedTextColor.GREEN : NamedTextColor.WHITE)),
                    Placeholder.unparsed("message", entry.message()));

            output.add(formattedLog);
        }
        return output;
    }
    public static List<AhelpEntry> getNotifications(OfflinePlayer player) {
        ArrayList<AhelpEntry> notifications = new ArrayList<>();
        try (Connection connection = plugin.getDatabase().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM ahelp_logs
                    WHERE player_uuid = ?
                    AND should_notify = 1
                    ORDER BY message_date DESC;
                    """);
            statement.setString(1, player.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            while(result.next()) {
                UUID playerUuid = UUID.fromString(result.getString("player_uuid"));
                UUID senderUuid = UUID.fromString(result.getString("sender_uuid"));
                String message = result.getString("message");
                Date timestamp = result.getDate("message_date");
                boolean isStaff = result.getBoolean("is_staff");
                boolean shouldNotify = result.getBoolean("should_notify");
                boolean bwoinked = result.getBoolean("bwoinked");
                notifications.add(new AhelpEntry(playerUuid, senderUuid, message, timestamp, isStaff, shouldNotify, bwoinked));
            }
        } catch (SQLException e) {
            plugin.logError(String.format("Failed to get pending notifications for %s ", player.getUniqueId()), e);
            return null;
        }
        return notifications;
    }
    public static void clearNotifications(OfflinePlayer player) {
        try (Connection connection = plugin.getDatabase().getConnection()) {
            List<AhelpEntry> notifications = getNotifications(player);
            if (notifications != null && !notifications.isEmpty()) {
                PreparedStatement statement = connection.prepareStatement("""
                        UPDATE ahelp_logs
                        SET should_notify = 0
                        WHERE player_uuid = ?;
                        """);
                statement.setString(1, player.getUniqueId().toString());
                statement.execute();
                plugin.log.info(String.format("Cleared notifications for %s", player.getName()));
            }
        } catch (SQLException e) {
           plugin.logError(String.format("Failed to clear notifications for %s ", player.getUniqueId()), e);
        }
    }
    public static Integer countPages(UUID playerUuid) {
        try (Connection connection = plugin.getDatabase().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT COUNT(*)
                    FROM ahelp_logs
                    WHERE player_uuid = ?;
                    """);
            statement.setString(1, playerUuid.toString());
            int resultCount = statement.executeQuery().getInt(1);
            return ((resultCount - 1) / MESSAGES_PER_PAGE + 1);
        } catch(SQLException e) {
            plugin.logError(String.format("Failed to count pages for %s", playerUuid), e);
            return null;
        }

    }
    public record AhelpEntry(UUID playerUuid, UUID senderUuid, String message, java.sql.Date timestamp, boolean isStaff, boolean shouldNotify, boolean bwoinked) {
        @Override
        public @NonNull String toString() {
            return String.format("%s%s%s -> %s: %s", bwoinked || !isStaff ? "" : "(S) ", isStaff ? "*" : "", senderUuid, playerUuid, message);
        }
    }
}

