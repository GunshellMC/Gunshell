package com.jazzkuh.gunshell.utils.command;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCommand implements TabExecutor {
    private CommandSender sender;
    private final String commandName;
    private final CommandArgument[] commandArguments;

    private final List<Method> subcommandMethods;

    public AbstractCommand(String commandName) {
        this.commandName = commandName;
        this.subcommandMethods = Arrays.stream(this.getClass().getMethods()).filter(method -> method.isAnnotationPresent(Subcommand.class)).collect(Collectors.toList());

        this.commandArguments = this.subcommandMethods.stream().map(method -> {
            Subcommand subcommand = method.getAnnotation(Subcommand.class);
            String usage = subcommand.usage().length() > 0 ? " " + subcommand.usage() : "";
            if (subcommand.permission()) {
                return new CommandArgument(subcommand.name() + usage, subcommand.description(), getBasePermission() + "." + subcommand.name());
            }

            return new CommandArgument(subcommand.name() + usage, subcommand.description());
        }).toArray(CommandArgument[]::new);
    }

    public AbstractCommand(String commandName, CommandArgument... commandArguments) {
        this.commandName = commandName;
        this.subcommandMethods = Arrays.stream(this.getClass().getMethods()).filter(method -> method.isAnnotationPresent(Subcommand.class)).collect(Collectors.toList());

        List<CommandArgument> subcommandArguments = this.subcommandMethods.stream().map(method -> {
            Subcommand subcommand = method.getAnnotation(Subcommand.class);
            String usage = subcommand.usage().length() > 0 ? " " + subcommand.usage() : "";
            if (subcommand.permission()) {
                return new CommandArgument(subcommand.name() + usage, subcommand.description(), getBasePermission() + "." + subcommand.name());
            }

            return new CommandArgument(subcommand.name() + usage, subcommand.description());
        }).collect(Collectors.toList());

        subcommandArguments.addAll(Arrays.asList(commandArguments));
        this.commandArguments = subcommandArguments.toArray(new CommandArgument[0]);
    }

    public void register(GunshellPlugin plugin) {
        this.register(plugin, false);
    }

    public void register(GunshellPlugin plugin, Boolean needsPermission) {
        PluginCommand cmd = plugin.getCommand(commandName);
        if (cmd != null) {
            if (needsPermission) {
                cmd.setPermission(getBasePermission());
                cmd.setPermissionMessage(ChatUtils.color(MessagesConfig.ERROR_NO_PERMISSION.get()));
            }
            cmd.setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        this.sender = sender;

        CommandInvocation commandInvocation = new CommandInvocation(sender, command, label, args);
        if (this.subcommandMethods.size() == 0) {
            this.execute(new CommandInvocation(sender, command, label, args));
            return true;
        }

        if (args.length >= 1) {
            for (Method method : this.subcommandMethods) {
                if (method.isAnnotationPresent(Subcommand.class)) {
                    Subcommand subcommand = method.getAnnotation(Subcommand.class);
                    List<String> aliases = new ArrayList<>();
                    if (subcommand.aliases().contains("|")) {
                        aliases = Arrays.asList(subcommand.aliases().split("\\|"));
                    } else if (subcommand.aliases().length() >= 1) {
                        aliases.add(subcommand.aliases());
                    }

                    if (!args[0].equalsIgnoreCase(subcommand.name()) && !aliases.contains(args[0].toLowerCase())) continue;
                    if (subcommand.permission() && !sender.hasPermission(getBasePermission() + "." + subcommand.name())) {
                        MessagesConfig.ERROR_NO_PERMISSION.get(sender);
                        return true;
                    }
                    if (subcommand.playerOnly() && !(sender instanceof Player)) return true;

                    try {
                        method.setAccessible(true);
                        method.invoke(this.getClass().getDeclaredConstructor().newInstance(),
                                commandInvocation);
                        return true;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        ChatUtils.sendMessage(sender, "&cSomething went wrong while executing this subcommand!");
                        return true;
                    }
                }
            }

            this.sendNotEnoughArguments(commandInvocation);
            return true;
        }

        this.execute(commandInvocation);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        CommandInvocation commandInvocation = new CommandInvocation(sender, command, label, args);
        List<String> options = new ArrayList<>();

        if (this.tabComplete(commandInvocation) != null) {
            options.addAll(this.tabComplete(commandInvocation));
        }

        if (args.length == 1 && this.subcommandMethods.size() >= 1) {
            for (Method method : this.subcommandMethods) {
                if (method.isAnnotationPresent(Subcommand.class)) {
                    Subcommand subcommand = method.getAnnotation(Subcommand.class);
                    if (subcommand.permission() && !sender.hasPermission(getBasePermission() + "." + subcommand.name())) continue;
                    options.add(subcommand.name());

                    if (subcommand.aliases().contains("|")) {
                        options.addAll(Arrays.asList(subcommand.aliases().split("\\|")));
                    }
                }
            }

            return getApplicableTabCompleters(args[0], options);
        }

        return this.tabComplete(commandInvocation) != null ? options : null;
    }

    public abstract void execute(CommandInvocation commandInvocation);

    public abstract List<String> tabComplete(CommandInvocation commandInvocation);

    protected boolean senderIsPlayer() {
        return sender instanceof Player;
    }

    protected boolean hasPermission(String permission, CommandSender commandSender) {
        if (commandSender.hasPermission(permission)) {
            return true;
        }

        MessagesConfig.ERROR_NO_PERMISSION.get(commandSender);
        return false;
    }

    protected void addIfPermission(CommandSender sender, Collection<String> options, String permission, String option) {
        if (sender.hasPermission(permission)) {
            options.add(option);
        }
    }

    protected String getBasePermission() {
        return "gunshell.commands." + this.commandName;
    }

    protected void sendNotEnoughArguments(CommandInvocation commandInvocation) {
        for (CommandArgument commandArgument : this.commandArguments) {
            if (commandArgument.getPermission() == null || commandInvocation.getCommandSender().hasPermission(commandArgument.getPermission())) {
                String usage = commandArgument.getArguments();
                commandInvocation.getCommandSender().sendMessage(ChatUtils.color("&6/" + commandInvocation.getCommand().getName() + " &7" + usage + "&8 - &7" + commandArgument.getDescription()));
            }
        }
    }

    protected List<String> getApplicableTabCompleters(String arg, Collection<String> completions) {
        return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
    }
}
