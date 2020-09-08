package net.omni.reports;

import net.omni.reports.commands.ReportCommand;
import net.omni.reports.commands.ReportsCommand;
import net.omni.reports.listeners.PlayerListener;
import net.omni.reports.report.CountdownHandler;
import net.omni.reports.report.PlayerUtil;
import net.omni.reports.report.ReportsHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReportsPlugin extends JavaPlugin {

    private PlayerUtil playerUtil;
    private ReportsHandler reportsHandler;
    private CountdownHandler countdownHandler;

    private String reportFormat;
    private boolean sql = false;
    private int countdownSeconds;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.sql = getConfig().getBoolean("use-mysql");
        this.reportFormat = String.join("\n", getConfig().getString("reportFormat"));
        this.countdownSeconds = getConfig().getInt("countdown-seconds");

        this.playerUtil = new PlayerUtil();

        this.reportsHandler = new ReportsHandler(this);
        reportsHandler.load();

        this.countdownHandler = new CountdownHandler(this);

        registerCommands();
        registerListeners();

        sendConsole("&aSuccessfully enabled OmniReports v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {

        reportsHandler.flush();
        countdownHandler.flush();

        sendConsole("&aSuccessfully disabled OmniReports");
    }

    public void sendConsole(String message) {
        sendMessage(Bukkit.getConsoleSender(), message);
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(translate("[PREFIX] " + message));
    }

    public String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getReportFormat(int id, String reporter, String reported, String reason) {
        return translate(reportFormat.
                replace("{id}", String.valueOf(id)).
                replace("{reporter}", reporter).
                replace("{reported}", reported).
                replace("{reason}", reason));
    }

    private void registerCommands() {
        new ReportCommand(this).register();
        new ReportsCommand(this).register();
    }

    private void registerListeners() {
        new PlayerListener(this).register();
    }

    public int getCountdownSeconds() {
        return countdownSeconds;
    }

    public boolean isSql() {
        return sql;
    }

    public PlayerUtil getPlayerUtil() {
        return playerUtil;
    }

    public ReportsHandler getReportsHandler() {
        return reportsHandler;
    }

    public CountdownHandler getCountdownHandler() {
        return countdownHandler;
    }
}
