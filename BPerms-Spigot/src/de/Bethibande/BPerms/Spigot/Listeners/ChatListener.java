package de.Bethibande.BPerms.Spigot.Listeners;

import de.Bethibande.BPerms.Groups.PermissionGroup;
import de.Bethibande.BPerms.Spigot.BPerms;
import de.Bethibande.BPerms.Spigot.PermissionManager.GroupStorrage;
import de.Bethibande.BPerms.Spigot.PermissionManager.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(BPerms.useChat) {
            String msg = e.getMessage();
            PermissionGroup parent = GroupStorrage.getGroup(UserManager.getPermissionUser(e.getPlayer()).getParent());
            String prefix = parent.getPrefix();
            String suffix = parent.getSuffix();
            if(prefix == null) prefix = "";
            if(suffix == null) suffix = "";
            e.setFormat(BPerms.config.getString("chat.pattern").replace("%prefix%", prefix).replaceAll("%player%", e.getPlayer().getName())
            .replaceAll("%suffix%", suffix).replaceAll("%message%", msg));
        }
    }

}
