package com.ementalo.commandalert.Commands;

import com.ementalo.commandalert.CommandAlert;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public interface ICommand {
    void execute(CommandSender sender, Command command, String commandLabel, String[]args, CommandAlert parent);
    void execute(Player player, Command command, String commandLabel, String[] args, CommandAlert parent);

}
