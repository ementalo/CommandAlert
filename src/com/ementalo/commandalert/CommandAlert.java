package com.ementalo.commandalert;

import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class CommandAlert extends JavaPlugin
{
	private CommandAlertPlayerListener playerListener = new CommandAlertPlayerListener(this);
	public Object permissions = null;
	Plugin permPlugin = null;
	Boolean isGm = false;
	static final Logger log = Logger.getLogger("Minecraft");

	@Override
	public void onDisable()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onEnable()
	{
		log.log(Level.INFO, "Checking for permission plugins....");
		permPlugin = this.getServer().getPluginManager().getPlugin("GroupManager");
		if (permPlugin == null)
		{
			permPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
			if (permPlugin == null)
			{
				log.log(Level.INFO, "[CommandAlert] Permissions plugins not found only ops can see the alerts");
				return;
			}
			else
			{
				log.log(Level.INFO, "[CommandAlert] Found Permissions. Using it for permissions");
			}
		}
		else
		{
			log.log(Level.INFO, "[CommandAlert] Found GroupManager. Using it for permissions");
			isGm = true;
		}

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Lowest, this);
	}

	public Boolean hasPermission(String node, Player base)
	{
		if (permPlugin == null && base.isOp())
			return true;
		if (isGm)
		{
			GroupManager gm = (GroupManager)permPlugin;
			return gm.getWorldsHolder().getWorldPermissions(base).has(base, node);

		}
		else
		{
			Permissions pm = (Permissions)permPlugin;
			return pm.getHandler().has(base, node);
		}
	}
}