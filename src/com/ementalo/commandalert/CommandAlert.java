package com.ementalo.commandalert;

import com.ementalo.commandalert.Commands.ICommand;
import com.ementalo.commandalert.Permissions.PermissionsBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;


public class CommandAlert extends JavaPlugin
{
	public CommandAlertPlayerListener playerListener = null;
	private CommandAlertServerListener serverListener = null;
	public static final Logger cmdAlertLog = Logger.getLogger("CommandAlert");
	static final Logger log = Logger.getLogger("Minecraft");
    PermissionsBase permsBase = null;
	public Config config = null;
	FileHandler fileHandle = null;


	public void onDisable()
	{
		log.log(Level.INFO, "[CommandAlert] disabled");
        if (cmdAlertLog != null)
        {
            cmdAlertLog.removeHandler(fileHandle);
        }
		if (fileHandle != null)
		{
			fileHandle.close();
		}
	}


	public void onEnable()
	{
		config = new Config(this);
		try
		{
			config.LoadSettings();
		}
		catch (Exception ex)
		{
			log.log(Level.SEVERE, "[CommandAlert] Could not load the config file", ex);
		}
		if (config.logToFile())
		{
			SetupLogging();
		}
        this.getServer().getPluginManager().registerEvents(serverListener = new CommandAlertServerListener(this), this);
        this.getServer().getPluginManager().registerEvents(playerListener = new CommandAlertPlayerListener(this), this);
		log.info("Loaded " + this.getDescription().getName() + " build " + this.getDescription().getVersion() + " maintained by " + this.getDescription().getAuthors());
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
			fileHandle = new FileHandler(this.getDataFolder() + "/logs/" + formatDateFromMs(System.currentTimeMillis(), "yyyy-MM-dd") + ".log", true);
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

        ICommand cmd;
        try {
            cmd = (ICommand)this.getClass().getClassLoader().loadClass("com.ementalo.commandalert.Commands.Command" + command.getName()).newInstance();
        } catch (Exception ex) {
            return false;
        }

        if (sender instanceof Player) {
            if (permsBase != null) {
                if (!permsBase.hasPermission((Player) sender, "commandalert." + commandLabel)) {
                    sender.sendMessage("§7You do not have permission for that command");
                    return true;
                }
            }

            cmd.execute((Player) sender, command, commandLabel, args, this);
        } else {
            cmd.execute(sender, command, commandLabel, args, this);
        }
        return true;
    }
}
