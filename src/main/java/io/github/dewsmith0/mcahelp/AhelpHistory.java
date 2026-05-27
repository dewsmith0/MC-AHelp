package io.github.dewsmith0.mcahelp;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class AhelpHistory {
    private static final MCAhelp plugin = MCAhelp.getPlugin(MCAhelp.class);
    public static void addLog(OfflinePlayer player, Player sender, String message, boolean isStaff) {
        try (Connection connection = plugin.getDatabase().getConnection()) {
            String sql = """
                    INSERT INTO ahelp_logs (
                        player_uuid, sender_uuid, message, is_staff
                    ) VALUES (?, ?, ?, ?);
                    """;
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, sender.getUniqueId().toString());
            statement.setString(3, message.strip());
            statement.setBoolean(4, isStaff);
            statement.execute();
        } catch (SQLException e) {
            plugin.logError(String.format("Failed to add ahelp log for %s -> %s: %s", sender.getUniqueId(), player.getUniqueId(), message));
            plugin.logError(e.getMessage());
        }
    }
    public static ArrayList<AhelpEntry> getLogs(OfflinePlayer player, int limit) {
        try (Connection connection = plugin.getDatabase().getConnection()){
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM ahelp_logs
                    WHERE player_uuid = ?
                    ORDER BY message_date ASC
                    LIMIT ?;
                    """);
            statement.setString(1, player.getUniqueId().toString());
            statement.setInt(2, limit);
            ResultSet result = statement.executeQuery();
            ArrayList<AhelpEntry> output = new ArrayList<>();
            while(result.next()) {
                UUID playerUuid = player.getUniqueId();
                UUID senderUuid = UUID.fromString(result.getString("sender_uuid"));
                String message = result.getString("message");
                Date timestamp = result.getDate("message_date");
                boolean isStaff = result.getBoolean("is_staff");
                AhelpEntry entry = new AhelpEntry(playerUuid, senderUuid, message, timestamp, isStaff);
                output.add(entry);
            }
            return output;
        } catch (SQLException e) {
            plugin.logError(String.format("Failed to get ahelp logs for %s: %s", player.getUniqueId(), e.getMessage()));
            return null;
        }
    }
    public record AhelpEntry(UUID playerUuid, UUID senderUuid, String message, Date timestamp, boolean isStaff) { }
}

