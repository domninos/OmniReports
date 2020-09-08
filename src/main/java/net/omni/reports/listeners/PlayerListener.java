package net.omni.reports.listeners;

import net.omni.reports.ReportsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final ReportsPlugin plugin;

    public PlayerListener(ReportsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("reports.alert")) {
            int size = plugin.getReportsHandler().size();

            if (size > 0)
                plugin.sendMessage(player, "&bThere are " + size + " reports available.");
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        plugin.getCountdownHandler().removeCooldown(event.getPlayer());
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
