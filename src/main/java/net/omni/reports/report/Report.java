package net.omni.reports.report;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Report {
    private final int id;

    private final String reporterString;
    private final UUID reporter;
    private final String reporterPlayer;

    private final String reportedString;
    private final UUID reported;
    private final String reportedPlayer;

    private final String reason;

    public Report(int id, String reporterString, String reportedString, String reason) {
        String[] reporterSplit = reporterString.split(":");

        if (reporterSplit.length <= 1)
            throw new IllegalArgumentException("Split length equal and/or lower than 1!");

        String reporterPlayer = reporterSplit[0];
        UUID reporter = UUID.fromString(reporterSplit[1]);

        String[] reportedSplit = reportedString.split(":");

        if (reportedSplit.length <= 1)
            throw new IllegalArgumentException("Split length equal and/or lower than 1!");

        String reportedPlayer = reportedSplit[0];
        UUID reported = UUID.fromString(reportedSplit[1]);

        this.id = id;

        this.reporterString = reporterString;
        this.reporter = reporter;
        this.reporterPlayer = reporterPlayer;

        this.reportedString = reportedString;
        this.reported = reported;
        this.reportedPlayer = reportedPlayer;

        this.reason = ChatColor.stripColor(reason);
    }

    public int getId() {
        return id;
    }

    public String getReporterString() {
        return reporterString;
    }

    public OfflinePlayer getReporter() {
        return Bukkit.getOfflinePlayer(reporter);
    }

    public String getReporterPlayer() {
        return reporterPlayer;
    }

    public String getReportedString() {
        return reportedString;
    }

    public OfflinePlayer getReported() {
        return Bukkit.getOfflinePlayer(reported);
    }

    public String getReportedPlayer() {
        return reportedPlayer;
    }

    public String getReason() {
        return reason;
    }
}
