package com.ementalo.commandalert.Commands;

import com.ementalo.commandalert.CommandAlert;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandcmdalertr implements ICommand {
    public void execute(CommandSender sender, Command command, String commandLabel, String[] args, CommandAlert parent) {

        if (commandLabel.equalsIgnoreCase("cmdalertr")){
            parent.reloadConfig();
            parent.playerListener.maxLocations = parent.config.getLocationHistory();
            parent.playerListener.alertLocations = new Location[parent.playerListener.maxLocations];
            sender.sendMessage("[CommandAlert] Reloaded Config");

        }
    }

    public void execute(Player player, Command command, String commandLabel, String[] args, CommandAlert parent) {
        execute((CommandSender) player, command, commandLabel, args, parent);
    }
}
