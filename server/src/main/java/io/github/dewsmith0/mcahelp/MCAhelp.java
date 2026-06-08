package io.github.dewsmith0.mcahelp;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public final class MCAhelp extends JavaPlugin {
    public final Logger log = this.getLogger();
    private DatabaseManager db;
    public final HashMap<UUID, AhelpRateLimit> rateLimits = new HashMap<>();
    @Override
    public void onEnable() {
        db = new DatabaseManager();
        db.connect();
        Config.load();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(AhelpCommand.createCommand("ahelp"));
            commands.registrar().register(ManageCommand.createCommand("mc-ahelp"));

        });

        getServer().getPluginManager().registerEvents(new AhelpEvents(), this);
        getLogger().info(String.format("Location: %s", getDataFolder().getAbsolutePath()));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public DatabaseManager getDatabase() {
        return db;
    }

    public void logError(String errorExplanation, Exception exception) {
        log.severe(String.join(errorExplanation, exception.getMessage()));
    }
}
