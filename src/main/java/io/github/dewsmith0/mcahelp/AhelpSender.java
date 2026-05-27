package io.github.dewsmith0.mcahelp;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class AhelpSender {
    private static final MCAhelp plugin = MCAhelp.getPlugin(MCAhelp.class);
    private static final MiniMessage mm = MiniMessage.miniMessage();
    public static void sendAhelpToAdmins(Player sender, String message) {
        if (message.isBlank()) return;
        Component formattedMessage = mm.deserialize("<aqua>[AHelp] <sender>:</aqua> <message>",
                Placeholder.unparsed("sender", sender.getName()),
                Placeholder.unparsed("message", message));
        AhelpHistory.addLog(sender, sender, message, false);
        for (OfflinePlayer operator : plugin.getServer().getOperators()) {
            if(operator.isOnline() && operator.getPlayer() != null) {
                operator.getPlayer().sendMessage(formattedMessage);
            }


        }
    }

    public static void sendAhelpToPlayer(Player sender, Player target, String message) {
        if (message.isBlank()) return;
        Component formattedMessage = mm.deserialize("<dark_aqua>[AHelp] <sender>:</dark_aqua> <message>",
                Placeholder.unparsed("sender", sender.getName()),
                Placeholder.unparsed("message", message));
        AhelpHistory.addLog(target, sender, message, true);
        target.sendMessage(formattedMessage);
        target.playSound(Sound.sound(Key.key("entity.player.splash_high_speed"), Sound.Source.UI, 2, 2), Sound.Emitter.self());
    }

}
