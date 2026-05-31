package io.github.dewsmith0.mcahelp;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.dialog.Dialog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

public class AhelpCommand {
    private static final MCAhelp plugin = JavaPlugin.getPlugin(MCAhelp.class);
    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(commandName);
        root.then(Commands.literal("asend")
                .requires(sender -> sender.getSender().hasPermission(AhelpPermissions.SEND_ADMIN))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .then(Commands.argument("bwoink", BoolArgumentType.bool())
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(AhelpCommand::runAdminSendCommand)))));
        root.then(Commands.literal("send")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(AhelpCommand::runPlayerSendCommand)));
        root.then(Commands.literal("open")
                .requires(sender -> sender.getSender().hasPermission(AhelpPermissions.OPEN_OTHER))
                .then(Commands.argument("player", ArgumentTypes.playerProfiles())
                        .executes(AhelpCommand::runAdminOpenCommand)
                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                .executes(AhelpCommand::runAdminOpenCommand))));
        root.then(Commands.literal("page")
                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                        .executes(AhelpCommand::runPageCommand)));
        root.executes(AhelpCommand::runPlayerOpenCommand);
        return root.build();
    }

    private static int runPlayerOpenCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can run this command.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        Dialog dialog = AhelpDialog.createPlayerDialog((Player) sender, 1);
        if (dialog == null) {
            sender.sendMessage(Component.text("Failed to load AHelp logs!", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        sender.showDialog(dialog);
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
    private static int runAdminOpenCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can run this command.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        if (!sender.hasPermission(AhelpPermissions.SEND_ADMIN)) {
            sender.sendMessage(Component.text("You do not have permission!", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        PlayerProfileListResolver resolver = ctx.getArgument("player", PlayerProfileListResolver.class);
        PlayerProfile profile;
        // TODO: make /ahelp open on offline players have a local database of players and not query mojang
        try {
            profile = (new ArrayList<>(resolver.resolve(ctx.getSource())).getFirst());
        } catch (CommandSyntaxException e) {
            sender.sendMessage(Component.text("Player does not exist!", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        OfflinePlayer target;
        UUID targetUuid = profile.getId();
        String targetName;
        if (targetUuid != null && profile.getName() != null) {
            target = plugin.getServer().getOfflinePlayer(targetUuid);
            targetName = profile.getName();
        } else {
            sender.sendMessage(Component.text("Failed to get profile!", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        Integer page;
        try {
            page = ctx.getArgument("page", Integer.class);
        } catch (IllegalArgumentException e) {
            page = 1;
        }
        Dialog dialog = AhelpDialog.createAdminDialog(target, page );
        if (dialog == null) {
            sender.sendMessage(Component.text("Failed to load AHelp logs!", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        sender.sendMessage(Component.text(targetName));
        sender.showDialog(dialog);
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
            boolean bwoink = ctx.getArgument("bwoink", boolean.class);
            if (message.matches(".*\\b-s\\b.*")) { sender.sendMessage("Bwoink"); bwoink = true; }
            AhelpSender.sendAhelpToPlayer((Player) sender, target, message, bwoink);
        } catch (CommandSyntaxException e) {
            sender.sendMessage(Component.text("Player is not online! Use /ahelp open instead!", NamedTextColor.RED));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int runPageCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can run this command.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        UUID senderUuid = ((Player) sender).getUniqueId();
        Integer totalPages = AhelpHistory.countPages(senderUuid);
        int page = Math.clamp(ctx.getArgument("page", int.class), 1, totalPages != null ? totalPages : 1);
        Dialog dialog;
        dialog = AhelpDialog.createPlayerDialog((Player) sender, page);
        if (dialog == null) {
            sender.sendMessage(Component.text("Failed to load AHelp logs!", NamedTextColor.RED));
            plugin.log.warning(String.format("Failed to switch AHelp page for %s", sender.getName()));
            return Command.SINGLE_SUCCESS;
        }
        sender.showDialog(dialog);

        return Command.SINGLE_SUCCESS;


    }
}
