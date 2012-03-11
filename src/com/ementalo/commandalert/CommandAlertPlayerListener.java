package com.ementalo.commandalert;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;



public class CommandAlertPlayerListener implements Listener {
    CommandAlert parent = null;
    public int maxLocations = 30;
    public Location[] alertLocations = null;
    public int index = 0;

    public CommandAlertPlayerListener(CommandAlert parent) {
        maxLocations = parent.config.getLocationHistory();
        alertLocations = new Location[maxLocations];
        this.parent = parent;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled() || event.getMessage().contains("cmdcheck".toLowerCase()) || parent.permsBase.hasPermission(player, "commandalert.notrigger"))
            return;
        String cmd = event.getMessage();


        if (parent.config.getMode().equalsIgnoreCase("whitelist") && parent.config.getCommandList().contains(cmd.split(" ")[0].replace("/", "").toLowerCase())) {
            return;
        }

        if (parent.config.getMode().equalsIgnoreCase("blacklist") && !parent.config.getCommandList().contains(cmd.split(" ")[0].replace("/", "").toLowerCase())) {
            if (!parent.config.getCommandList().contains("*")) {
                return;
            }
        }

        //reset the index
        if (index == maxLocations) {
            index = 0;
        }
        alertLocations[index] = player.getLocation();

        if (parent.config.showInGame()) {
            for (Player p : parent.getServer().getOnlinePlayers()) {
                if (player.equals(p)) {
                    continue;
                }
                if (parent.permsBase.hasPermission(p, "commandalert.alerts")) {
                    p.sendMessage(FormatAlert(player, cmd));
                }
            }
        }
        if (parent.config.logToFile()) {
            LogToFile(FormatAlert(player, cmd) + " at " + FormatCoords(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
        }
        if (parent.config.logToConsole()) {
            CommandAlert.log.info(ChatColor.stripColor(FormatAlert(player, cmd) + " at " + FormatCoords(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ())));
        }
        index++;

    }

    public String FormatAlert(Player player, String command) {
        return "[" + ChatColor.AQUA + index + ChatColor.WHITE + "] " + player.getDisplayName() + " used command: " + command;
    }

    public String FormatCoords(double x, double y, double z) {
        DecimalFormat fmt = new DecimalFormat("0.##");
        return "X= " + String.valueOf(fmt.format(x)) + " Y=" + String.valueOf(fmt.format(y)) + " Z=" + String.valueOf(fmt.format(z));
    }

    public void LogToFile(String formattedAlert) {
        CommandAlert.cmdAlertLog.info(ChatColor.stripColor(formattedAlert));
    }
}