package io.github.dewsmith0.mcahelp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static Connection connection;

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/MC-AHelp/database.db");
        } catch (ClassNotFoundException | SQLException e) {
            MCAhelp.getPlugin(MCAhelp.class).getLogger().severe("Failed to load AHelp database!");
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
                      `is_staff` BOOLEAN(1) DEFAULT 0
                    )""").execute();
        } catch (SQLException e) {
            MCAhelp.getPlugin(MCAhelp.class).getLogger().severe("Failed to init database!");
            MCAhelp.getPlugin(MCAhelp.class).getLogger().severe(e.getMessage());
        }
    }
}