package net.omni.reports.report;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtil {

    public UUID getUUID(String string) {
        String[] split = split(string);

        if (split.length <= 1)
            throw new IllegalArgumentException("Split length equal and/or lower than 1!");

        return UUID.fromString(split[1]);
    }

    public Player getPlayer(String string) {
        String[] split = split(string);

        if (split.length <= 1)
            throw new IllegalArgumentException("Split length equal and/or lower than 1!");

        return Bukkit.getPlayerExact(split[0]);
    }

    public String getPlayerName(String string) {
        String[] split = split(string);

        if (split.length <= 1)
            throw new IllegalArgumentException("Split length equal and/or lower than 1!");

        return split[0];
    }

    public String getDBValue(Player player) {
        if (player == null)
            return "null:null";

        return player.getName() + ":" + player.getUniqueId().toString();
    }

    private String[] split(String string) {
        return string.split(":");
    }
}
