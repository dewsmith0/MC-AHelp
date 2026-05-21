package io.github.dewsmith0.mcahelp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AhelpCommand {
    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(commandName);
        root.executes(AhelpCommand::runBaseCommand);
        return root.build();
    }

    private static int runBaseCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can run this command.", Style.style(NamedTextColor.RED)));
            return Command.SINGLE_SUCCESS;
        }
        sender.showDialog(AhelpDialog.createDialog((Player) sender));
        return Command.SINGLE_SUCCESS;

    }
}
