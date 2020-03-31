package de.Bethibande.BPerms.Bungee.listeners;

import de.Bethibande.BPerms.Bungee.BPerms;
import de.Bethibande.BPerms.Bungee.GroupStorrage;
import de.Bethibande.BPerms.Bungee.UserManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CommandListener implements Listener {

    @EventHandler
    public void onCommand(ChatEvent e) {
        if(e.getSender() instanceof ProxiedPlayer) {
            if(e.getMessage().equalsIgnoreCase("/bperms reload")) {
                GroupStorrage.reloadFromSQL();
                UserManager.updateAllPlayers();
                ((ProxiedPlayer) e.getSender()).sendMessage(BPerms.prefix + "BPerms §9Bungee §7reloaded!");
            }
        }
    }

}
