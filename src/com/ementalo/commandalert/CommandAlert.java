package com.ementalo.commandalert;

import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;


public class CommandAlert extends JavaPlugin
{
	private CommandAlertPlayerListener playerListener = null;
	public Logger cmdAlertLog = Logger.getLogger("CommandAlert");
	static final Logger log = Logger.getLogger("Minecraft");
	private static Yaml yaml = new Yaml(new SafeConstructor());
	public Object permissions = null;
	Plugin permPlugin = null;
	Boolean isGm = false;
	public Configuration config = null;
	FileHandler fileHandle = null;
	@Override
	public void onDisable()
	{
		log.log(Level.INFO, "[CommandAlert] disabled");
		fileHandle.close();
	}
	
	@Override
	public void onEnable()
	{
		
		SetupLogging();
		try
		{
			LoadSettings();
		}
		catch (Exception ex)
		{
			log.log(Level.SEVERE, "[CommandAlert] Could not load the config file", ex);
		}
		playerListener = new CommandAlertPlayerListener(this);
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
		File logDir = new File(this.getDataFolder(), "logs/");
		if (!logDir.exists())
		{
			logDir.mkdirs();
		}
		try
		{
			cmdAlertLog.setUseParentHandlers(false);
			cmdAlertLog.setLevel(Level.INFO);
			fileHandle = new FileHandler(this.getDataFolder() + "/logs/" + formatDateFromMs(System.currentTimeMillis(), "yyyy-MM-dd") + ".log");
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
			return formatDateFromMs(millisecs, "yyyy-MM-dd HH:mm:ss ");
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
	
	public String formatDateFromMs(long millisecs, String format)
	{
		SimpleDateFormat date_format = new SimpleDateFormat(format);
		Date resultdate = new Date(millisecs);
		return date_format.format(resultdate);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage("Commands can only be used in game");
			return true;
		}
		
		Player player = (Player)sender;
		if (commandLabel.equalsIgnoreCase("cmdcheck") && hasPermission("commandalert.cmdcheck", player))
		{
			Location playerLocation = playerListener.alertLocations[args.length < 1 ? playerListener.index -1 : Integer.parseInt(args[0])];
			if (args.length < 1)
			{
				player.sendMessage("Teleporting to last location history");
				player.teleport(playerLocation);
				return true;
			}
			if (args.length == 1)
			{
				if (playerListener.alertLocations[Integer.parseInt(args[0])] == null)
				{
					player.sendMessage(ChatColor.RED + "That id is not present in the current location history list");
				}
			}
			if (args.length == 3)
			{
				Location loc = new Location(playerLocation.getWorld(), Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), player.getLocation().getYaw(), player.getLocation().getPitch());
				player.sendMessage("Teleporting...");
				player.teleport(loc);
				return true;
			}
			if (args.length > 3)
			{
				return false;
			}
		}
		return true;
	}
	
	public void LoadSettings() throws Exception
	{
		if (!this.getDataFolder().exists())
		{
			this.getDataFolder().mkdirs();
		}
		File commandAlert = new File(this.getDataFolder(), "CommandAlert.yml");
		if (!commandAlert.exists()) commandAlert.createNewFile();
		config = new Configuration(commandAlert);
		Map<String, Object> data = (Map<String, Object>)yaml.load(new FileReader(commandAlert));
		if (data == null)
		{
			log.info("[CommandAlert] Generating CommandAlert config file.");
			data = new HashMap<String, Object>();
			data.put("mode", "blacklist");
			data.put("commands", "warp, home, spawn");
			data.put("logToFile", false);
			data.put("locationHistory", 30);
			FileWriter tx = new FileWriter(commandAlert);
			tx.write(yaml.dump(data));
			tx.flush();
			tx.close();
		}
		config.load();
	}
	
	public Boolean logToFile()
	{
		return config.getBoolean("logToFile", false);
	}
	
	public String getMode()
	{
		return config.getString("mode", "blacklist");
	}
	
	public ArrayList<String> getCommandList()
	{
		ArrayList<String> cmds = new ArrayList<String>();
		for (String cmd : config.getString("commands", "warp, home, spawn").split(","))
		{
			cmd = cmd.trim();
			if (cmd.isEmpty())
			{
				continue;
			}
			cmds.add(cmd.toLowerCase());
		}
		return cmds;
	}
	
	public Integer getLocationHistory()
	{
		return config.getInt("locationHistory", 30);
	}
}
