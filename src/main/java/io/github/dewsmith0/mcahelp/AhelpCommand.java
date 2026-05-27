package io.github.dewsmith0.mcahelp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AhelpCommand {
    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(commandName);
        root.then(Commands.literal("asend")
                .requires(sender -> sender.getSender().hasPermission("mcahelp.adminsend"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(AhelpCommand::runAdminSendCommand))));
        root.then(Commands.literal("send")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(AhelpCommand::runPlayerSendCommand)));
        root.executes(AhelpCommand::runBaseCommand);
        return root.build();
    }

    private static int runBaseCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can run this command.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        sender.showDialog(AhelpDialog.createPlayerDialog((Player) sender));
        return Command.SINGLE_SUCCESS;

    }

    private static int runPlayerSendCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can run this command.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        String message = ctx.getArgument("message", String.class);
        AhelpSender.sendAhelpToAdmins((Player) sender, message);

        return Command.SINGLE_SUCCESS;
    }

    private static int runAdminSendCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can run this command.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        if (!sender.hasPermission("mcahelp.adminsend")) {
            sender.sendMessage(Component.text("You do not have permission!", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        try {
            PlayerSelectorArgumentResolver resolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
            Player target = resolver.resolve(ctx.getSource()).getFirst();
            String message = ctx.getArgument("message", String.class);
            AhelpSender.sendAhelpToPlayer((Player) sender, target, message);
        } catch (CommandSyntaxException e) {
            sender.sendMessage(Component.text("Syntax error!", NamedTextColor.RED));
        }
        return Command.SINGLE_SUCCESS;
    }
}
