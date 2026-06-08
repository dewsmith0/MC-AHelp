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
        AhelpHistory.addLog(new AhelpHistory.AhelpEntry(
                sender.getUniqueId(), sender.getUniqueId(), message,
                null,false, false, false));
        sender.sendMessage(formattedMessage);
        for (Player player : plugin.getServer().getOnlinePlayers().stream().filter(
                player -> player.hasPermission(AhelpPermissions.RECEIVE_ADMIN)).toList()) {
            if  (player != sender) {
                 player.sendMessage(formattedMessage);
            }
        }
    }

    public static void sendAhelpToPlayer(Player sender, OfflinePlayer target, String message, boolean bwoink) {
        if (message.isBlank()) return;
        if (!sender.hasPermission(AhelpPermissions.SEND_ADMIN)) {
            plugin.log.warning(String.format("%s tried to adminsend without permission!", sender.getName()));
            return;
        }
        String targetName = target.getName() != null ? target.getName() : "<ERROR>";
        Component formattedMessage = mm.deserialize("<dark_aqua>[AHelp] <sender>:</dark_aqua> <message>",
                Placeholder.unparsed("sender", sender.getName()),
                Placeholder.unparsed("message", message));
        Component senderMessage = mm.deserialize("<dark_aqua>[AHelp] <sender> -> <target>:</dark_aqua> <message>",
                Placeholder.unparsed("sender", sender.getName()),
                Placeholder.unparsed("message", message),
                Placeholder.unparsed("target", targetName));

        boolean shouldNotify;
        if (target.isConnected() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(formattedMessage);
            shouldNotify = false;
            if (bwoink) {
                for (int i = 0; i < Config.bwoinkStackCount; i++) {
                    target.getPlayer().playSound(Sound.sound(Key.key(Config.bwoinkSoundId),Sound.Source.MASTER, Config.bwoinkVolume, Config.bwoinkPitch), Sound.Emitter.self());
                }
            }
        } else if (!Config.offlineAhelpsEnabled) {
            sender.sendMessage(Component.text("Could not send this message, as the player is currently offline, and the server administrator has disabled offline AHelps.", NamedTextColor.RED));
            return;
        } else {
            shouldNotify = true;
            sender.sendMessage(Component.text("This player is currently offline, and will be notified the next time they join.", NamedTextColor.YELLOW));
        }
        AhelpHistory.addLog(new AhelpHistory.AhelpEntry(target.getUniqueId(), sender.getUniqueId(), message, null, true, shouldNotify, bwoink));
        sender.sendMessage(senderMessage);
    }

}
