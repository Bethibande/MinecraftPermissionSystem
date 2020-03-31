package de.Bethibande.BPerms.Bungee;

import de.Bethibande.BPerms.Bungee.listeners.CommandListener;
import de.Bethibande.BPerms.Bungee.listeners.ConnectListener;
import de.Bethibande.BPerms.Bungee.listeners.PermissionEvent;
import de.Bethibande.BPerms.utils.FileUtils;
import de.Bethibande.BPerms.utils.MySQL;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;

public class BPerms extends Plugin {

    public static final String prefix = "§b§lBPerms §8┃ §7";

    public static SQLConfig sql = new SQLConfig();

    public void onEnable() {
        try {
            File sqlFile = new File(getDataFolder() + "/sql.yml");
            if (!sqlFile.exists()) {
                if (!sqlFile.getParentFile().exists()) {
                    sqlFile.getParentFile().mkdirs();
                    sqlFile.getParentFile().mkdir();
                }
                sqlFile.createNewFile();
                SQLConfig cfg = new SQLConfig();
                FileUtils.saveJson(sqlFile, cfg);
            }

            sql = (SQLConfig)FileUtils.loadJson(sqlFile);
            MySQL mysql = new MySQL(sql.host, sql.database, sql.username, sql.password, sql.port, sql.useSSL);
            MySQL.INSTANCE = mysql;

            GroupStorrage.init();
            GroupStorrage.loadGroups();
            UserManager.init();

            getProxy().getPluginManager().registerListener(this, new CommandListener());
            getProxy().getPluginManager().registerListener(this, new ConnectListener());
            getProxy().getPluginManager().registerListener(this, new PermissionEvent());

            if(BungeeCord.getInstance().getPlayers().size() > 0) {
                System.out.println("[BPerms] There are already players online, was the server reloaded?");
                UserManager.updateAllPlayers();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
