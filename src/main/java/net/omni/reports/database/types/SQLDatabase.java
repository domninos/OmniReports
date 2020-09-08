package net.omni.reports.database.types;

import net.omni.reports.ReportsPlugin;
import net.omni.reports.database.DatabaseHandler;
import net.omni.reports.report.Report;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class SQLDatabase implements ReportsDatabase {
    private final ReportsPlugin plugin;

    private final DatabaseHandler databaseHandler;
    private final String table_name = "reports";

    public SQLDatabase(ReportsPlugin plugin) {
        this.plugin = plugin;

        if (!plugin.isSql())
            throw new IllegalArgumentException("You cannot access MySQL database because it is using config database.");

        final int port = plugin.getConfig().getInt("mysql.port");
        final String host = plugin.getConfig().getString("mysql.host");
        final String database = plugin.getConfig().getString("mysql.database");
        final String username = plugin.getConfig().getString("mysql.username");
        final String password = plugin.getConfig().getString("mysql.password");

        this.databaseHandler = new DatabaseHandler(host, port, database, username, password, plugin);
    }

    @Override
    public void load() {
        databaseHandler.open();

        databaseHandler.query("CREATE TABLE IF NOT EXISTS `" + table_name + "` ( " +
                "`id` varchar(4) NOT NULL, " +
                "`reporter` varchar(128) NOT NULL, " +
                "`reported` varchar(128) NOT NULL, " +
                "`reason` varchar(256) NOT NULL, " +
                "UNIQUE KEY `id` (`id`) " +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");

        plugin.sendConsole("&aLoaded the " + table_name + " table.");
    }

    @Override
    public void reportPlayer(int id, Player reporter, Player reported, String reason) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                databaseHandler.query("INSERT IGNORE INTO `" + table_name + "` (`id`,`reporter`,`reported`,`reason`) VALUES (" +
                        "'" + id + "'," +
                        "'" + plugin.getPlayerUtil().getDBValue(reporter) + "'," +
                        "'" + plugin.getPlayerUtil().getDBValue(reported) + "'," +
                        "'" + ChatColor.stripColor(reason) + "'" +
                        ");"));
    }

    @Override
    public void deleteReport(int id) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                databaseHandler.query("DELETE FROM `" + table_name + "` WHERE `id`='" + id + "';"));
    }

    @Override
    public void flush() {
        databaseHandler.close();
        plugin.sendConsole("&aClosed MySQL connection.");
    }

    @Override
    public Set<Report> getReports() {
        Set<Report> reports = new HashSet<>();

        try {
            ResultSet rs = databaseHandler.query("SELECT * FROM `" + table_name + "`").getResultSet();

            while (rs.next()) {
                int id = rs.getInt("id");
                String reporter = rs.getString("reporter");
                String reported = rs.getString("reported");
                String reason = rs.getString("reason");

                reports.add(new Report(id, reporter, reported, reason));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }
}