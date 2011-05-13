package com.ementalo.commandalert;

import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
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
	public Logger cmdAlertLog = Logger.getLogger("CommandAlert");
	static final Logger log = Logger.getLogger("Minecraft");
	public Object permissions = null;
	Plugin permPlugin = null;
	Boolean isGm = false;

	@Override
	public void onDisable()
	{
		log.log(Level.INFO, "[CommandAlert] disabled");
	}

	@Override
	public void onEnable()
	{
		SetupLogging();
		log.info("Loaded " + this.getDescription().getName() + " build " + this.getDescription().getVersion() + " maintained by " + this.getDescription().getAuthors());
		log.log(Level.INFO, "[CommandAlert] Checking for permission plugins....");
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

	public void SetupLogging()
	{
		File logDir = new File(this.getDataFolder(), "cmdLog/");
		if (!logDir.exists())
		{
			logDir.mkdirs();
		}
		try
		{
			cmdAlertLog.setUseParentHandlers(false);
			cmdAlertLog.setLevel(Level.INFO);
			FileHandler fileHandle = new FileHandler(this.getDataFolder() + "logs/" + (int)(System.currentTimeMillis() / 1000L) + ".log");
			fileHandle.setFormatter(new CommandAlertLog());
			cmdAlertLog.addHandler(fileHandle);

		}
		catch (SecurityException e1)
		{
			log.log(Level.WARNING, "[CommandAlert] Could not create log file", e1);
		}
		catch (IOException e1)
		{
			log.log(Level.WARNING, "[CommandAlert] Could not create log file", e1);
		}
	}


	class CommandAlertLog extends Formatter
	{
		@Override
		public String format(LogRecord rec)
		{
			return calcDate(rec.getMillis()) + "[INFO] " + formatMessage(rec) + "\r\n";
		}

		private String calcDate(long millisecs)
		{
			SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
			Date resultdate = new Date(millisecs);
			return date_format.format(resultdate);
		}

		@Override
		public String getHead(Handler h)
		{
			return "";
		}

		@Override
		public String getTail(Handler h)
		{
			return "";
		}
	}
}