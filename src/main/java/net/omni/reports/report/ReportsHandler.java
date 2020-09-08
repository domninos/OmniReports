package net.omni.reports.report;

import net.omni.reports.ReportsPlugin;
import net.omni.reports.database.types.ConfigDatabase;
import net.omni.reports.database.types.ReportsDatabase;
import net.omni.reports.database.types.SQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ReportsHandler {
    protected final SQLDatabase sqlDatabase;
    protected final ConfigDatabase configDatabase;
    private final Set<Report> reports = new HashSet<>();
    private final ReportsPlugin plugin;
    private ReportsDatabase reportsDatabase;

    public ReportsHandler(ReportsPlugin plugin) {
        this.plugin = plugin;

        this.sqlDatabase = new SQLDatabase(plugin);
        this.configDatabase = new ConfigDatabase(plugin);

        if (plugin.isSql())
            setDatabase(sqlDatabase);
        else
            setDatabase(configDatabase);

        plugin.sendConsole("&aUsing " + (plugin.isSql() ? "MySQL" : "flat-file") + " database..");

        reportsDatabase.load();
    }

    public void setDatabase(ReportsDatabase reportsDatabase) {
        this.reportsDatabase = reportsDatabase;
    }

    public void switchDatabase() {
        reportsDatabase.flush();

        if (plugin.isSql()) {
            setDatabase(configDatabase);
            plugin.setSql(false);
        } else {
            setDatabase(sqlDatabase);
            plugin.setSql(true);
        }

        reportsDatabase.load();

        plugin.sendConsole("&aSuccessfully switched databases.");
    }

    public void load() {
        reports.clear();

        plugin.sendConsole("&aLoading " + (plugin.isSql() ? "MySQL" : "flat-file") + " reports..");

        if (plugin.isSql()) {
            CompletableFuture<Set<Report>> future = CompletableFuture.supplyAsync(reportsDatabase::getReports);

            future.whenComplete((futureReports, throwable) -> {
                if (throwable != null) {
                    plugin.sendConsole("&cSomething went wrong loading reports.");
                    return;
                }

                this.reports.addAll(futureReports);
                plugin.sendConsole("&aSuccessfully gathered reports.");
            });
        } else
            reports.addAll(reportsDatabase.getReports());

        plugin.sendConsole("&aSuccessfully loaded reports.");
    }

    public void report(Player reporter, Player reported, String reason) {
        int id = reports.size() + 1;
        String reporterFormat = plugin.getPlayerUtil().getDBValue(reporter);
        String reportedFormat = plugin.getPlayerUtil().getDBValue(reported);

        reports.add(new Report(id, reporterFormat, reportedFormat, ChatColor.stripColor(reason)));
        reportsDatabase.reportPlayer(id, reporter, reported, reason);

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("reports.alert"))
                player.sendMessage(plugin.getReportFormat(id, reporter.getName(),
                        reported.getName(), ChatColor.stripColor(reason)));
        });

        // TODO possibly bungee ??
    }

    public void deleteReport(int id, CommandSender sender) {
        Report report = getReport(id);

        if (report == null) {
            plugin.sendMessage(sender, "&cReport with ID of " + id + " not found.");
            return;
        }

        reports.remove(report);
        reportsDatabase.deleteReport(id);
        plugin.sendConsole("&aDeleted report #" + id);
    }

    public Report getReport(int id) {
        return reports.stream().filter(report -> report.getId() == id).findFirst().orElse(null);
    }

    public Set<Report> getReport(String reported) {
        return reports.stream().filter(report -> report.getReportedPlayer().equals(reported)).collect(Collectors.toSet());
    }

    public Set<Report> getReports(String reporter) {
        return reports.stream().filter(report -> report.getReporterPlayer().equals(reporter)).collect(Collectors.toSet());
    }

    public boolean isReported(String player) {
        return getReport(player).size() > 0;
    }

    public boolean isReported(String player, String reporter) {
        return getReport(player).stream().anyMatch(report -> report.getReporterPlayer().equals(reporter));
    }

    public Report[] getArrayReports() {
        return reports.toArray(new Report[0]);
    }

    public int size() {
        return reports.size();
    }

    public void flush() {
        reports.clear();
        reportsDatabase.flush();
    }

    /*

        if (plugin.isSql()) {
            // TODO save to config database

            ConfigDatabase configDatabase = new ConfigDatabase(plugin);
            configDatabase.load();

            for (Report report : reports) {
                if (report == null)
                    continue;

                String path = "reports." + report.getId() + ".";

                configDatabase.set(path + "reporter", report.getReporterString());
                configDatabase.set(path + "reported", report.getReportedString());
                configDatabase.set(path + "reason", report.getReason());
            }

            configDatabase.save();
            configDatabase.flush();
        } else {
            // TODO save to mysql database

            SQLDatabase sqlDatabase = new SQLDatabase(plugin);
            sqlDatabase.load();

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                for (Report report : reports) {
                    if (report == null)
                        continue;

                    sqlDatabase.query("INSERT IGNORE INTO `" + sqlDatabase.getTable() +
                            "` (`id`,`reporter`,`reported`,`reason`) VALUES (" +
                            "'" + report.getId() + "'," +
                            "'" + report.getReporterString() + "'," +
                            "'" + report.getReportedString() + "'," +
                            "'" + report.getReason() + "'" +
                            ");");
                }
            });

            sqlDatabase.flush();
        }

     */
}
