package com.ementalo.commandalert;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;


public class CommandAlertPlayerListener extends PlayerListener
{
	CommandAlert parent = null;
	int maxLocations = 30;
	Location[] alertLocations = null;
	int index = 0;

	public CommandAlertPlayerListener(CommandAlert parent)
	{
		maxLocations = parent.getLocationHistory();
		alertLocations = new Location[maxLocations];
		this.parent = parent;
	}

	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if (event.isCancelled() || event.getMessage().contains("cmdcheck".toLowerCase())) return;
		String cmd = event.getMessage();

		if (parent.getMode().equalsIgnoreCase("whitelist") && parent.getCommandList().contains(cmd.split(" ")[0].replace("/", "").toLowerCase()))
		{
			return;
		}

		if (parent.getMode().equalsIgnoreCase("blacklist") && !parent.getCommandList().contains(cmd.split(" ")[0].replace("/", "").toLowerCase()))
		{
			if (!parent.getCommandList().contains("*"))
			{
				return;
			}
		}

		//reset the index
		if (index == maxLocations)
		{
			index = 0;
		}

		for (Player p : parent.getServer().getOnlinePlayers())
		{
			if (parent.hasPermission("commandalert.alerts", p) && !parent.hasPermission("commandalert.noalerts", p))
			{
				alertLocations[index] = event.getPlayer().getLocation();
				p.sendMessage(FormatAlert(event.getPlayer(), cmd));
				if (parent.logToFile())
				{
					LogToFile(FormatAlert(event.getPlayer(), cmd));
				}
				index++;
			}
		}

	}

	public String FormatAlert(Player player, String command)
	{
		return "[" + ChatColor.AQUA + index + ChatColor.WHITE + "] " + player.getDisplayName() + " used command: " + command;
	}

	public void LogToFile(String formattedAlert)
	{
		CommandAlert.cmdAlertLog.info(formattedAlert);
	}
}