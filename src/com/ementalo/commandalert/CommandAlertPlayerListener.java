
package com.ementalo.commandalert;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;


public class CommandAlertPlayerListener extends PlayerListener
{
	
	CommandAlert parent = null;
	Location[] alertLocations = new Location[30];
	int index = 0;

	public CommandAlertPlayerListener(CommandAlert parent)
	{
		this.parent = parent;
	}

	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		//reset the index
		if(index == 30) 
		{index=0;}
		if (event.isCancelled()) return;
		String cmd = event.getMessage();
		

		for (Player p : parent.getServer().getOnlinePlayers())
		{
			if (parent.hasPermission("commandalert.alerts", p) && !parent.hasPermission("commandalert.noalerts", p))
			{
				alertLocations[index] = event.getPlayer().getLocation();
				p.sendMessage(FormatAlert(event.getPlayer(), cmd));
				LogToFile(FormatAlert(event.getPlayer(), cmd));				
				if(index == 30)
				{index = 0;}
				index++;
			}
		}

	}

	public String FormatAlert(Player player, String command)
	{
		return  "[" + ChatColor.AQUA + index + ChatColor.WHITE + "] " + player.getDisplayName() + " used command: " + command ;
	}
	
	public void LogToFile(String formattedAlert)
	{
		parent.cmdAlertLog.info(formattedAlert);
	}
}