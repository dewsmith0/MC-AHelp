package io.github.dewsmith0.mcahelp;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public final class MCAhelp extends JavaPlugin {
    private static final Logger log = LogManager.getLogger(MCAhelp.class);
    private DatabaseManager db;
    @Override
    public void onEnable() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(AhelpCommand.createCommand("ahelp"));
        });
        getLogger().info(String.format("Location: %s", getDataFolder().getAbsolutePath()));
       db = new DatabaseManager();
       db.connect();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public DatabaseManager getDatabase() {
        return db;
    }

    public void logError(String error) {
        getLogger().severe(error);
    }
}
