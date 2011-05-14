package com.ementalo.commandalert;

import java.text.DecimalFormat;
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
		Player player = event.getPlayer();
		if (event.isCancelled() || event.getMessage().contains("cmdcheck".toLowerCase()) || parent.hasPermission("commandalert.notrigger", player)) return;
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
			if (parent.hasPermission("commandalert.alerts", p))
			{
				alertLocations[index] = player.getLocation();
				p.sendMessage(FormatAlert(player, cmd));
				if (parent.logToFile())
				{
					LogToFile(FormatAlert(player, cmd) +" at " + FormatCoords(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
				}
				index++;
			}
		}

	}

	public String FormatAlert(Player player, String command)
	{
		return "[" + ChatColor.AQUA + index + ChatColor.WHITE + "] " + player.getDisplayName() + " used command: " + command;
	}

	public String FormatCoords(double x, double y, double z)
	{
		DecimalFormat fmt = new DecimalFormat("0.##");
		return "X= " + String.valueOf(fmt.format(x)) + " Y=" + String.valueOf(fmt.format(y)) + " Z=" + String.valueOf(fmt.format(z));
	}

	public void LogToFile(String formattedAlert)
	{
		CommandAlert.cmdAlertLog.info(formattedAlert);
	}
}