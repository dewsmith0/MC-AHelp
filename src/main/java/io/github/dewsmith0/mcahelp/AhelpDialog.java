package io.github.dewsmith0.mcahelp;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class AhelpDialog {
    public static Dialog createDialog(OfflinePlayer player) {
        DialogBody body = DialogBody.plainMessage(Component.text("Messages failed to load", Style.style(NamedTextColor.RED, TextDecoration.ITALIC))) ;
        DialogInput messageField = DialogInput.text("message_input",Component.text("Send a message"))
                .maxLength(200)
                .width(600)
                .build();

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Staff Help")).body(List.of(body)).inputs(List.of(messageField)).build())
                .type(DialogType.notice(ActionButton.builder(Component.text("Send!", Style.style(NamedTextColor.GREEN, TextDecoration.BOLD))).build())));

        return dialog;
    }
}
