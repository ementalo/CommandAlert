package com.ementalo.commandalert.Permissions;

import org.bukkit.entity.Player;

public class BukkitPerms extends PermissionsBase {

    @Override
    public boolean hasPermission(Player player, String node) {
        return player.hasPermission(node);
    }
}
