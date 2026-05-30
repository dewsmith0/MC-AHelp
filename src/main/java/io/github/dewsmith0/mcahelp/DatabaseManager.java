package io.github.dewsmith0.mcahelp;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static Connection connection;
    private static final MCAhelp plugin = JavaPlugin.getPlugin(MCAhelp.class);

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/MC-AHelp/database.db");
        } catch (ClassNotFoundException | SQLException e) {
            plugin.logError("Failed to load AHelp database!", e);
        } finally {
            initializeDatabase();
        }
    }
    public Connection getConnection() {
        connect();
        return connection;
    }
    public void initializeDatabase() {
        try {
            connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS `ahelp_logs` (
                      `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                      `player_uuid` CHAR(36) NOT NULL,
                      `sender_uuid` CHAR(36) NOT NULL,
                      `message` VARCHAR(512) NULL DEFAULT "",
                      `message_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      `is_staff` BOOLEAN(1) DEFAULT 0,
                      `should_notify` BOOLEAN(1) DEFAULT 0,
                      `bwoinked` BOOLEAN(1) DEFAULT 0
                    )""").execute();
        } catch (SQLException e) {
            plugin.logError("Failed to init AHelp database!", e);
        }
    }
}