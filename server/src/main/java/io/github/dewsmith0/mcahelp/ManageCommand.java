package io.github.dewsmith0.mcahelp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ManageCommand {
    private static final MCAhelp plugin = JavaPlugin.getPlugin(MCAhelp.class);
    public static LiteralCommandNode<CommandSourceStack> createCommand(final String commandName) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(commandName);
        root.then(Commands.literal("reload-config")
                .requires(sender -> sender.getSender().hasPermission(AhelpPermissions.RELOAD_CONFIG))
                .executes(ManageCommand::reloadConfigCommand)
        );
        return root.build();
    }
    private static int reloadConfigCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        try {
            plugin.getConfig().load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.logError("An error occurred while reloading the MC-AHelp config!: ", e);
            return Command.SINGLE_SUCCESS;
        }
        Config.load();
        sender.sendMessage(Component.text("[MC-AHelp] Config has been reloaded!", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }
}
