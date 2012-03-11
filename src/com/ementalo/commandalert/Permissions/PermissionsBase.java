package com.ementalo.commandalert.Permissions;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.entity.Player;

public abstract class PermissionsBase {

    protected GroupManager groupManager = null;
    protected com.nijikokun.bukkit.Permissions.Permissions permissions = null;

    abstract public boolean hasPermission(Player player, String node);

}
