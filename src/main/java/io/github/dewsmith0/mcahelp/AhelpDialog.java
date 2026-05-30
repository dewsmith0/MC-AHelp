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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AhelpDialog {
    private static DialogBase createBaseDialog(OfflinePlayer targetPlayer, int limit, Component title, boolean isAdmin) {
        ArrayList<AhelpHistory.AhelpEntry> entries = AhelpHistory.getLogs(targetPlayer, limit);
        List<DialogBody> body = new ArrayList<>();
        List<DialogInput> inputs = new ArrayList<>();
        if (entries == null) {
            return null;
        }
        inputs.add(DialogInput.text("message_input", Component.text("Send a message"))
                .maxLength(200)
                .width(600)
                .build());
        if (isAdmin) {
            inputs.add(DialogInput.bool("bwoink", Component.text("''Bwoink''?")).build());
        }
        body.add(DialogBody.plainMessage(Component.join(JoinConfiguration.newlines(), AhelpHistory.parseLogs(entries))));

        return DialogBase.create(title, null, true, false, DialogBase.DialogAfterAction.CLOSE, body, inputs);
    }

    public static Dialog createPlayerDialog(OfflinePlayer player, int limit) {
        DialogBase base = createBaseDialog(player, limit, Component.text("Admin Help"), false);
        if (base == null) return null;
        List<ActionButton> buttons = getButtons(false, null);
        AhelpHistory.clearNotifications(player);
        return Dialog.create(builder -> builder.empty()
                .base(base)
                .type(DialogType.confirmation(buttons.getFirst(), buttons.getLast())));
    }

    public static Dialog createAdminDialog(OfflinePlayer target, int limit) {
        DialogBase base = createBaseDialog(target, limit, Component.text(String.format("Admin Help for: %s", target.getName())), true);
        if (base == null) return null;
        List<ActionButton> buttons = getButtons(true, target);
        return Dialog.create(builder -> builder.empty()
                .base(base)
                .type(DialogType.confirmation(buttons.getFirst(), buttons.getLast())));

    }


    private static List<ActionButton> getButtons(boolean isAdmin, OfflinePlayer target) {
        List<ActionButton> inputs = new ArrayList<>();
        inputs.add(ActionButton.create(
                Component.text("Send"),
                Component.text("Click here to send the message"),
                200,
                DialogAction.customClick((view, audience) -> {
                            String message = view.getText("message_input");
                            if (isAdmin) {
                                Boolean bwoink = view.getBoolean("bwoink");
                                if (audience instanceof Player viewer && (message != null) && bwoink != null) {
                                    AhelpSender.sendAhelpToPlayer(viewer, target, message, bwoink);
                                }
                            } else {
                                if (audience instanceof Player viewer && message != null) {
                                    AhelpSender.sendAhelpToAdmins(viewer, message);
                                }
                            }
                        },
                        ClickCallback.Options.builder()
                                .uses(1)
                                .lifetime(ClickCallback.DEFAULT_LIFETIME)
                                .build())));

        inputs.add(ActionButton.create(
                Component.text("Close"),
                Component.text("Click to exit (your message won't be sent)"), 100,
                null));
        return inputs;
    }
}
