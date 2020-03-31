package de.Bethibande.BPerms.Bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PermissionEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPermissionCheck(PermissionCheckEvent e) {
        if(e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer)e.getSender();
            String permission = e.getPermission();
            for(String perm : p.getPermissions()) {
                if(perm.endsWith("*")) {
                    if(permission.startsWith(perm.substring(0, perm.length()-1))) {
                        e.setHasPermission(true);
                    }
                    if(perm.equalsIgnoreCase(permission)) e.setHasPermission(true);
                }
                if(perm.equalsIgnoreCase(permission)) e.setHasPermission(true);
            }
        }
    }

}
