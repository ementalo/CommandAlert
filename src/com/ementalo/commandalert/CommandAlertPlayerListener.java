/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ementalo.commandalert;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;


/**
 *
 * @author devhome
 */
public class CommandAlertPlayerListener extends PlayerListener
{
	CommandAlert parent = null;
	public HashMap map = new HashMap();
	Location[] alertLocations = new Location[30];
	int index = 0;

	public CommandAlertPlayerListener(CommandAlert parent)
	{
		this.parent = parent;
	}

	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		
		if (event.isCancelled()) return;
		String cmd = event.getMessage();
		

		for (Player p : parent.getServer().getOnlinePlayers())
		{
			//if (parent.hasPermission("commandalert.alerts", p) && !parent.hasPermission("commandalert.noalerts", p))
			//{
				
				p.sendMessage(FormatAlert(event.getPlayer(), cmd));
				
				alertLocations[0] = event.getPlayer().getLocation();
				index++;
	//		}
		}

	}

	public String FormatAlert(Player player, String command)
	{
		String x = String.valueOf(player.getLocation().getX());
		String y = String.valueOf(player.getLocation().getY());
		String z = String.valueOf(player.getLocation().getZ());

		return  "[" + ChatColor.AQUA + index + ChatColor.WHITE + "] " + player.getDisplayName() + " used command: " + command + " at X=" + x + " Y=" + y + " Z=" + z;
	}
}
