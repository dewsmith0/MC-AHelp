package io.github.dewsmith0.mcahelp;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AhelpDialog {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final MCAhelp plugin = MCAhelp.getPlugin(MCAhelp.class);
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static Dialog createPlayerDialog(OfflinePlayer player) {
        ArrayList<AhelpHistory.AhelpEntry> entries = AhelpHistory.getLogs(player, 20);
        DialogBody body;
        if (entries == null) {
            return Dialog.create(builder -> builder.empty()
                    .base(DialogBase.builder(Component.text("Error while loading AHelp logs"))
                            .body(List.of(DialogBody.plainMessage(Component.text("Messages failed to load")))).build())
                    .type(DialogType.notice()));
        }
        ArrayList<Component> logBody = new ArrayList<>();
        for (AhelpHistory.AhelpEntry entry : entries) {
            String senderName = plugin.getServer().getOfflinePlayer(entry.senderUuid()).getName();
            String formattedDate = dateFormat.format(entry.timestamp());
            if (senderName == null) {
                senderName = "<ERROR>";
            }
            Component formattedLog = mm.deserialize("<gray>[<date>]</gray> <sender><reset>: <message>",
                    Placeholder.unparsed("date", formattedDate),
                    Placeholder.component("sender", Component.text(senderName, entry.isStaff() ? NamedTextColor.GREEN : NamedTextColor.WHITE)),
                    Placeholder.unparsed("message", entry.message()));

            logBody.add(formattedLog);
        }
        body = DialogBody.plainMessage(Component.join(JoinConfiguration.newlines(), logBody), 512);
        DialogInput messageField = DialogInput.text("message_input", Component.text("Send a message"))
                .maxLength(200)
                .width(600)
                .build();

        ActionButton sendButton = ActionButton.create(
                Component.text("Send"),
                Component.text("Click here to send the message"),
                200,
                DialogAction.customClick((view, audience) -> {
                    String message = view.getText("message_input");
                    if (audience instanceof Player viewer && message != null) {
                        AhelpSender.sendAhelpToAdmins(viewer, message);
                    }
                }, ClickCallback.Options.builder()
                        .uses(1)
                        .lifetime(ClickCallback.DEFAULT_LIFETIME)
                        .build()));
        ActionButton closeButton = ActionButton.create(
                Component.text("Close"),
                Component.text("Click to exit (your message won't be sent)"), 100,
                DialogAction.customClick(
                        (view, response) -> {
                        },
                        ClickCallback.Options.builder().uses(1).lifetime(ClickCallback.DEFAULT_LIFETIME).build()));

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(
                                Component.text("AHelp Log"))
                        .body(List.of(body))
                        .inputs(List.of(messageField))
                        .afterAction(DialogBase.DialogAfterAction.CLOSE)
                        .build())
                .type(DialogType.confirmation(sendButton, closeButton)));
    }
}

