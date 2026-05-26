package io.github.dewsmith0.mcahelp;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AhelpDialog {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final MCAhelp plugin = MCAhelp.getPlugin(MCAhelp.class);

    public static Dialog createDialog(OfflinePlayer player) {
       // DialogBody body = DialogBody.plainMessage(Component.text("Messages failed to load", Style.style(NamedTextColor.RED, TextDecoration.ITALIC))) ;

        ArrayList<AhelpHistory.AhelpEntry> entries = AhelpHistory.getLogs(player, 20);
        StringBuilder joined = new StringBuilder();
        DialogBody body;
        if (entries == null) {
            return Dialog.create(builder -> builder.empty()
                    .base(DialogBase.builder(Component.text("Error while loading AHelp logs"))
                            .body(List.of(DialogBody.plainMessage(Component.text("Messages failed to load")))).build())
                    .type(DialogType.notice()));
        }
        for (AhelpHistory.AhelpEntry entry : entries) {
            String senderName = plugin.getServer().getOfflinePlayer(entry.senderUuid()).getName();
            String formattedDate = dateFormat.format(entry.timestamp());
            String formattedLog = String.format("\n[%s] %s: %s", formattedDate, senderName, entry.message());
            joined.append(formattedLog);
        }
        body = DialogBody.plainMessage(Component.text(joined.toString()));
        DialogInput messageField = DialogInput.text("message_input",Component.text("Send a message"))
                .maxLength(200)
                .width(600)
                .build();

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("AHelp Log")).body(List.of(body)).inputs(List.of(messageField)).build())
                .type(DialogType.notice(ActionButton.builder(Component.text("Close")).build())));
    }
}
