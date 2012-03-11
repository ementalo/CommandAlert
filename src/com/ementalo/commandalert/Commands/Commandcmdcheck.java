package com.ementalo.commandalert.Commands;


import com.ementalo.commandalert.CommandAlert;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commandcmdcheck implements ICommand{
    public void execute(CommandSender sender, Command command, String commandLabel, String[] args, CommandAlert parent) {
        sender.sendMessage("Command can only be used in game");
    }

    public void execute(Player player, Command command, String commandLabel, String[] args, CommandAlert parent) {
        int id = 0;
        try
        {
            id = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e)
        {
            return;
        }
        Location playerLocation = parent.playerListener.alertLocations[args.length < 1 ? parent.playerListener.index - 1 : id];
        if (args.length < 1)
        {
            if (playerLocation == null)
            {
                player.sendMessage("§That location is no longer in the history");
                return;
            }
            player.sendMessage("Teleporting to location history");
            player.teleport(playerLocation);
            return;
        }
        if (args.length == 1)
        {
            if (parent.playerListener.alertLocations[id] == null)
            {
                player.sendMessage(ChatColor.RED + "That id is not present in the current location history list");
                return;
            }
            player.sendMessage("Teleporting to last location history");
            player.teleport(playerLocation);
            return;

        }
        if (args.length > 1)
        {
            return;
        }
    }
}
