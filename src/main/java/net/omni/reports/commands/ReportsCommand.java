package net.omni.reports.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.omni.reports.ReportsPlugin;
import net.omni.reports.report.Report;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

public class ReportsCommand implements CommandExecutor {
    private final ReportsPlugin plugin;

    public ReportsCommand(ReportsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("reports.use"))) {
            plugin.sendMessage(sender, "&cInsufficient permissions.");
            return true;
        }

        if (args.length == 0) {
            plugin.sendMessage(sender, "&cUsage: /reports <page> | /reports delete <id>");
            return true;
        } else if (args.length == 1) {
            if (NumberUtils.isNumber(args[0])) {
                if (!(sender.hasPermission("reports.reports"))) {
                    plugin.sendMessage(sender, "&cInsufficient permissions.");
                    return true;
                }

                int pageNumber;

                try {
                    pageNumber = Integer.parseInt(args[0]);
                } catch (NumberFormatException exception) {
                    pageNumber = 1;
                    plugin.sendMessage(sender, "&cInvalid number");
                }

                if (pageNumber <= 0)
                    pageNumber = 1;

                sendReports(sender, pageNumber);
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (!(sender.hasPermission("reports.delete"))) {
                    plugin.sendMessage(sender, "&cInsufficient permissions.");
                    return true;
                }

                plugin.sendMessage(sender, "&cUsage: /reports delete <id>");
            } else if (args[0].equalsIgnoreCase("switchDB")) {
                if (!sender.isOp()) {
                    plugin.sendMessage(sender, "&cInsufficient permissions.");
                    return true;
                }

                if (plugin.isSql())
                    plugin.sendMessage(sender, "&aSwitching from MySQL to flat-file database..");
                else
                    plugin.sendMessage(sender, "&aSwitching from flat-file to MySQL database..");

                plugin.getReportsHandler().switchDatabase();
                plugin.sendMessage(sender, "&aSuccessfully switched databases.");
            } else
                plugin.sendMessage(sender, "&cUsage: /reports <page> | /reports delete <id>");

            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete")) {
                if (!(sender.hasPermission("reports.delete"))) {
                    plugin.sendMessage(sender, "&cInsufficient permissions.");
                    return true;
                }

                int id;

                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    plugin.sendMessage(sender, "&cInvalid number.");
                    return true;
                }

                plugin.getReportsHandler().deleteReport(id, sender);
            } else
                plugin.sendMessage(sender, "&cUsage: /reports <page> | /reports delete <id>");

            return true;
        }

        return true;
    }

    public void register() {
        PluginCommand pluginCommand = plugin.getCommand("reports");

        if (pluginCommand != null)
            pluginCommand.setExecutor(this);
    }

    private void sendReports(CommandSender sender, int pageNumber) {
        Report[] array = plugin.getReportsHandler().getArrayReports();

        for (int i = pageNumber * 10; i < (pageNumber * 10) + 10; i++) {
            Report report = array[i];

            if (report != null) {
                TextComponent main = new TextComponent(
                        ChatColor.WHITE + "[" + ChatColor.AQUA + report.getId() + ChatColor.WHITE + "] " +
                                ChatColor.DARK_BLUE + report.getReporterPlayer() + ChatColor.BLUE + "reported " +
                                ChatColor.DARK_BLUE + report.getReportedPlayer());

                ComponentBuilder builder = new ComponentBuilder(ChatColor.AQUA + "ID: " + report.getId());

                builder.append(ChatColor.DARK_AQUA + "Reporter: " + report.getReporterPlayer());
                builder.append(ChatColor.DARK_AQUA + "Reported: " + report.getReportedPlayer());
                builder.append(ChatColor.DARK_AQUA + "Reason: " + report.getReason());

                main.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, builder.create()));

                sender.spigot().sendMessage(main);
            }
        }

        plugin.sendMessage(sender, "&3[" + pageNumber + "/" + array.length + "]");
    }

    /*
                    plugin.translate(
                            "&f[&b" + report.getId() + "&f] &9" +
                                    report.getReporterPlayer() + " &3reported &9" + report.getReportedPlayer())

            Report[] array = (Report[]) Arrays.stream(plugin.getReportsHandler().getArrayReports()).limit(10).toArray();

            for (Report report : array) {
                if (report != null)
                    sender.sendMessage(plugin.translate(
                            "&f[&b" + report.getId() + "&f] &9" +
                                    report.getReporterPlayer() + " &3reported &9" + report.getReportedPlayer() +
                                    " &3for &9'" + report.getReason() + "'"));
            }



            Report[] array = plugin.getReportsHandler().getArrayReports();

            for (int i = page * 10; i < (page * 10) + 10; i++) {
                Report report = array[i];

                if (report != null)
                    sender.sendMessage(plugin.translate(
                            "&f[&b" + report.getId() + "&f] &9" +
                                    report.getReporterPlayer() + " &3reported &9" + report.getReportedPlayer() +
                                    " &3for &9'" + report.getReason() + "'"));
            }

            plugin.sendMessage(sender, "&3[" + page + "/" + array.length + "]");
     */
}
