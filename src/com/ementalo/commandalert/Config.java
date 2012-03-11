package com.ementalo.commandalert;

import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.Set;

public class Config {
    
    CommandAlert parent;
    Configuration config;
    ArrayList<String> commandList;
    
    public Config(CommandAlert parent)
    {
        this.parent = parent;
        this.config = parent.getConfig();
        commandList = new ArrayList<String>();
    }

    public void LoadSettings() throws Exception
    {
        if (!parent.getDataFolder().exists())
        {
            parent.getDataFolder().mkdirs();
        }

        final Set<String> keys = config.getKeys(true);
        if (!keys.contains("mode"))
            config.set("mode", "blacklist");
        if (!keys.contains("commands"))
            config.set("commands", "warp, home, spawn");
        if (!keys.contains("showInGameAlert"))
            config.set("showInGameAlert", true);
        if (!keys.contains("logToConsole"))
            config.set("logToConsole", false);
        if (!keys.contains("logToFile"))
            config.set("logToFile", false);
        if (!keys.contains("locationHistory"))
            config.set("locationHistory", 30);
        if (!keys.contains("useBukkitPerms"))
            config.set("useBukkitPerms", false);
        parent.saveConfig();
        parent.reloadConfig();
        loadCommandList();
    }

    public Boolean logToFile()
    {
        return config.getBoolean("logToFile", false);
    }

    public Boolean getUseBukkitPerms()
    {
        return config.getBoolean("settings.useBukkitPerms", false);
    }

    public Boolean showInGame()
    {
        return config.getBoolean("showInGameAlert", true);
    }

    public Boolean logToConsole()
    {
        return config.getBoolean("logToConsole", false);
    }

    public String getMode()
    {
        return config.getString("mode", "blacklist");
    }

    public ArrayList<String> getCommandList()
    {
        return commandList;
    }

    public void loadCommandList()
    {
        for (String cmd : config.getString("commands", "warp, home, spawn").split(","))
        {
            cmd = cmd.trim();
            if (cmd.isEmpty())
            {
                continue;
            }
            commandList.add(cmd.toLowerCase());
        }
     }

    public Integer getLocationHistory()
    {
        return config.getInt("locationHistory", 30);
    }
}
