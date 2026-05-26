package io.github.dewsmith0.mcahelp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class AhelpSender {
    private static final MCAhelp plugin = MCAhelp.getPlugin(MCAhelp.class);
    public static void sendAhelpToAdmins(Player sender, String message) {
        Component formattedMessage = MiniMessage.miniMessage().deserialize("<light_aqua>[AHelp] <sender>:</light_aqua> <message>",
                Placeholder.unparsed("sender", sender.getName()), Placeholder.unparsed("message", message));
        AhelpHistory.addLog(sender, sender, message);
        for (OfflinePlayer operator : plugin.getServer().getOperators()) {
            if(operator.isOnline() && operator.getPlayer() != null) {
                operator.getPlayer().sendMessage(formattedMessage);

            }


        }
    }
}
