package net.omni.reports.database.types;

import net.omni.reports.report.Report;
import org.bukkit.entity.Player;

import java.util.Set;

public interface ReportsDatabase {

    void load();

    void reportPlayer(int id, Player reporter, Player reported, String reason);

    void deleteReport(int id);

    Set<Report> getReports();

    void flush();
}
