package net.omni.reports.database.types;

import net.omni.reports.ReportsPlugin;
import net.omni.reports.report.Report;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ConfigDatabase implements ReportsDatabase {
    private final File file;
    private final ReportsPlugin plugin;
    private FileConfiguration config;

    public ConfigDatabase(ReportsPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "database.yml");
        this.plugin = plugin;
    }

    @Override
    public void load() {
        if (!(file.exists())) {
            plugin.saveResource("database.yml", false);
            plugin.sendConsole("&aCreated database.yml.");
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void reportPlayer(int id, Player reporter, Player reported, String reason) {
        String path = "reports." + id + ".";

        set(path + "reporter", plugin.getPlayerUtil().getDBValue(reporter));
        set(path + "reported", plugin.getPlayerUtil().getDBValue(reported));
        set(path + "reason", ChatColor.stripColor(reason));
        save();
    }

    @Override
    public void deleteReport(int id) {
        set("reports." + id, null);
        save();
    }

    @Override
    public void flush() {
    }

    @Override
    public Set<Report> getReports() {
        Set<Report> reports = new HashSet<>();
        ConfigurationSection section = getSection("reports");

        if (section == null) {
            plugin.sendConsole("&aReports not found.");
            return reports;
        }

        for (String key : section.getKeys(false)) {
            int id = Integer.parseInt(key);
            String reporterString = section.getString(key + ".reporter");

            if (reporterString == null) {
                plugin.sendConsole("&cReporter of " + key + " not found.");
                continue;
            }

            String reportedString = section.getString(key + ".reported");

            if (reportedString == null) {
                plugin.sendConsole("&cReported of " + key + " not found.");
                continue;
            }

            String reason = section.getString(key + ".reason");

            if (reason == null) {
                plugin.sendConsole("&cReason of " + key + " not found.");
                continue;
            }

            reports.add(new Report(id, reporterString, reportedString, reason));
        }

        return reports;
    }

    public void set(String path, Object object) {
        config.set(path, object);
    }

    public ConfigurationSection getSection(String path) {
        return config.getConfigurationSection(path);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
