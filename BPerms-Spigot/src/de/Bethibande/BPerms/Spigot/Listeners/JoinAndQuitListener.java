package de.Bethibande.BPerms.Spigot.Listeners;

import de.Bethibande.BPerms.Spigot.PermissionManager.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndQuitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        UserManager.userConnected(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UserManager.userQuit(e.getPlayer());
    }

}
