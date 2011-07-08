package com.ementalo.commandalert;

import java.util.logging.Level;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;


public class CommandAlertServerListener extends ServerListener
{
	CommandAlert parent = null;

	public CommandAlertServerListener(CommandAlert parent)
	{
		this.parent = parent;
	}

	@Override
	public void onPluginEnable(PluginEnableEvent event)
	{
		if (parent.permPlugin != null) return;

		String pluginName = event.getPlugin().getDescription().getName();
		if (pluginName.equalsIgnoreCase("Permissions"))
		{
			parent.permPlugin = event.getPlugin();
			CommandAlert.log.log(Level.INFO, "[CommandAlert] Found " + pluginName + ". Using it for permissions");
		}
	}

	@Override
	public void onPluginDisable(PluginDisableEvent event)
	{
		if (parent.permPlugin == null) return;
		String pluginName = event.getPlugin().getDescription().getName();
		if (pluginName.equalsIgnoreCase("Permissions"))
		{
			parent.permPlugin = null;
			CommandAlert.log.log(Level.INFO, "[CommandAlert] " + pluginName + " disabled, using OPS.txt");
		}
	}
}
