package de.Bethibande.BPerms.Bungee.listeners;

import de.Bethibande.BPerms.Bungee.UserManager;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ConnectListener implements Listener {

    @EventHandler
    public void onConnect(PostLoginEvent e) {
        UserManager.userConnected(e.getPlayer());
        UserManager.loadPermissions(UserManager.getPermissionUser(e.getPlayer()));
    }

}
