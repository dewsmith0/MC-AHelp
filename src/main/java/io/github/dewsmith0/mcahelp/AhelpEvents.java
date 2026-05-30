package io.github.dewsmith0.mcahelp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
        List<AhelpHistory.AhelpEntry> notifications = AhelpHistory.getNotifications(event.getPlayer());
        if (notifications == null) {
            plugin.log.warning(String.format("Failed to get notifications for %s", event.getPlayer().getName()));
            return;
        }
        Collections.reverse(notifications);
        plugin.log.info("notification has been executed");
        List<Component> parsedLogs = AhelpHistory.parseLogs(notifications);
        if (!parsedLogs.isEmpty()) {
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(
                    "<yellow>You have <green><count></green> new AHelp message<plural>. Use <green>/ahelp</green> to view all new messages and clear this alert." +
                            "<br>The last few messages are shown below.</yellow>",
                    Placeholder.unparsed("count", String.valueOf(parsedLogs.size())),
                    Placeholder.unparsed("plural", parsedLogs.size() > 1 ? "s" : "")
            ));
            for (Component log : parsedLogs.subList(Math.max(parsedLogs.size() - 5, 0), parsedLogs.size())) {
                event.getPlayer().sendMessage(log);
            }
        }
    }

}
