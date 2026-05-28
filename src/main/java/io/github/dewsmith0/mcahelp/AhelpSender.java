package io.github.dewsmith0.mcahelp;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            if (operator.isOnline() && operator.getPlayer() != null) {
                operator.getPlayer().sendMessage(formattedMessage);
            }


        }
    }

    public static void sendAhelpToPlayer(Player sender, OfflinePlayer target, String message, boolean bwoink) {
        if (message.isBlank()) return;
        if (!sender.hasPermission(AhelpPermissions.SEND_ADMIN)) {
            plugin.getLogger().warning(String.format("%s tried to adminsend without permission!", sender.getName()));
            return;
        }
        Component formattedMessage = mm.deserialize("<dark_aqua>[AHelp] <sender>:</dark_aqua> <message>",
                Placeholder.unparsed("sender", sender.getName()),
                Placeholder.unparsed("message", message));
        Component senderMessage = mm.deserialize("<dark_aqua>[AHelp] <sender> -> <target>:</dark_aqua> <message>",
                Placeholder.unparsed("sender", sender.getName()),
                Placeholder.unparsed("message", message),
                Placeholder.unparsed("target", target.getName()));

        AhelpHistory.addLog(target, sender, message, true);
        if (target.isConnected() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(formattedMessage);
            if (bwoink) {
                target.getPlayer().playSound(Sound.sound(Key.key("entity.player.splash.high_speed"), Sound.Source.MASTER, 2, 1), Sound.Emitter.self());

            }
        } else {
            sender.sendMessage(Component.text("This player is currently offline, and will be notified the next time they join.", NamedTextColor.YELLOW));
            // TODO: make it actually notify them
        }
        sender.sendMessage(senderMessage);
    }

}
