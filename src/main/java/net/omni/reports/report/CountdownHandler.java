package net.omni.reports.report;

import net.omni.reports.ReportsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CountdownHandler {
    private final Map<Player, Integer> countdowns = new HashMap<>();
    private final ReportsPlugin plugin;
    private final int taskID;

    public CountdownHandler(ReportsPlugin plugin) {
        this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Map.Entry<Player, Integer> entry : countdowns.entrySet()) {
                if (entry.getValue() <= 0) {
                    removeCooldown(entry.getKey());
                    continue;
                }

                entry.setValue(entry.getValue() - 1);
            }
        }, 20L, 20L);
        this.plugin = plugin;
    }

    public void addCooldown(Player player) {
        addCooldown(player, plugin.getCountdownSeconds());
    }

    public void addCooldown(Player player, int cooldown) {
        if (!countdowns.containsKey(player))
            countdowns.put(player, cooldown);
        else
            countdowns.put(player, countdowns.get(player) + cooldown);
    }

    public boolean hasCooldown(Player player) {
        return countdowns.containsKey(player);
    }

    public int getCooldown(Player player) {
        return countdowns.getOrDefault(player, 0);
    }

    public void removeCooldown(Player player) {
        countdowns.remove(player);
    }

    public void flush() {
        countdowns.clear();

        if (Bukkit.getScheduler().isCurrentlyRunning(taskID))
            Bukkit.getScheduler().cancelTask(taskID);
    }
}