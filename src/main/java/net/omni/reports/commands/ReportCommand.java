package net.omni.reports.commands;

import net.omni.reports.ReportsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {
    private final ReportsPlugin plugin;

    public ReportCommand(ReportsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage(sender, "&cOnly palyers can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args.length == 1) {
            plugin.sendMessage(player, "&cUsage: /report <player> <reason>");
            return true;
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                plugin.sendMessage(player, "&cPlayer not found.");
                return true;
            }

            if (plugin.getCountdownHandler().hasCooldown(player)) {
                plugin.sendMessage(player, "&cYou are currently in cooldown! You can report in "
                        + plugin.getCountdownHandler().getCooldown(player) + " seconds.");
                return true;
            }

            StringBuilder reason = new StringBuilder();

            for (int i = 1; i < args.length; i++)
                reason.append(args[i]).append(" ");

            plugin.getReportsHandler().report(player, target, reason.toString());
            plugin.getCountdownHandler().addCooldown(player);
            plugin.sendMessage(player, "&aYou have reported " + target.getName() + " for &3" + reason.toString());
        }

        return true;
    }

    public void register() {
        PluginCommand pluginCommand = plugin.getCommand("report");

        if (pluginCommand != null)
            pluginCommand.setExecutor(this);
    }
}
