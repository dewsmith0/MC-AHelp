package io.github.dewsmith0.mcahelp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class AhelpEvents implements Listener {
    private static final MCAhelp plugin = JavaPlugin.getPlugin(MCAhelp.class);
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.rateLimits.put(player.getUniqueId(), new AhelpRateLimit(Config.rateLimitThreshold));
        if (!Config.offlineAhelpsEnabled) return;
        List<AhelpHistory.AhelpEntry> notifications = AhelpHistory.getNotifications(player);
        if (notifications == null) {
            plugin.log.warning(String.format("Failed to get notifications for %s", player.getName()));
            return;
        }
        Collections.reverse(notifications);
        plugin.log.info("notification has been executed");
        List<Component> parsedLogs = AhelpHistory.parseLogs(notifications);
        if (!parsedLogs.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<yellow>You have <green><count></green> new AHelp <message>. Use <green>/ahelp</green> to view all new messages and clear this alert.<preview></yellow>",
                    Placeholder.unparsed("count", String.valueOf(parsedLogs.size())),
                    Placeholder.unparsed("message", parsedLogs.size() > 1 ? "messages" : "message"),
                    Placeholder.parsed("preview", Config.maxNotifications > 0 ? "<br>The last few messages are shown below." : "")
            ));
            if (Config.maxNotifications > 0) {
                for (Component log : parsedLogs.subList(Math.max(parsedLogs.size() - Config.maxNotifications, 0), parsedLogs.size())) {
                    player.sendMessage(log);
                }
            }
        }
    }
}
