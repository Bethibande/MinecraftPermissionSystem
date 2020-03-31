package de.Bethibande.BPerms.Spigot;

import de.Bethibande.BPerms.Spigot.Commands.CMDBperms;
import de.Bethibande.BPerms.Spigot.Listeners.ChatListener;
import de.Bethibande.BPerms.Spigot.Listeners.JoinAndQuitListener;
import de.Bethibande.BPerms.Spigot.PermissionManager.GroupStorrage;
import de.Bethibande.BPerms.Spigot.PermissionManager.UserManager;
import de.Bethibande.BPerms.utils.MySQL;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BPerms extends JavaPlugin {

    // special characters »«┃

    public static final String prefix = "§b§lBPerms §8┃ §7";

    public static String bperms_command_permission = "bperms.admin";

    public static MySQL mysql;
    public static YamlConfiguration sqlConfig = new YamlConfiguration();
    public static FileConfiguration config;
    public static File sqlFile;

    public static List<TextComponent> help = new ArrayList<>();

    public static boolean useChat;

    private static BPerms plugin;
    public void onEnable() {
        try {
            plugin = this;

            sqlFile = new File(getDataFolder() + "/mysql.yml");
            if(!sqlFile.exists()) {
                if(!sqlFile.getParentFile().exists()) {
                    sqlFile.getParentFile().mkdirs();
                    sqlFile.getParentFile().mkdir();
                }
                sqlFile.createNewFile();
                sqlConfig.load(sqlFile);
                sqlConfig.set("mysql.host", "localhost");
                sqlConfig.set("mysql.username", "user");
                sqlConfig.set("mysql.password", "password");
                sqlConfig.set("mysql.database", "database");
                sqlConfig.set("mysql.port", 3306);
                sqlConfig.set("mysql.useSSL", true);
                sqlConfig.save(sqlFile);
            } else sqlConfig.load(sqlFile);

            mysql = new MySQL(sqlConfig.getString("mysql.host"), sqlConfig.getString("mysql.database"), sqlConfig.getString("mysql.username"), sqlConfig.getString("mysql.password"), sqlConfig.getInt("mysql.port"), sqlConfig.getBoolean("mysql.useSSL"));
            MySQL.INSTANCE = mysql;

            config = getConfig();
            if(config.getString("chat.use") == null) {
                config.set("chat.use", true);
                config.set("chat.pattern", "%prefix% %player% %suffix% %message%");
                saveCfg();
            }

            useChat = config.getBoolean("chat.use");

            GroupStorrage.init();
            UserManager.init();
            System.out.println("[BPerms] Loading groups..");
            GroupStorrage.loadGroups();

            if(GroupStorrage.getGroups().isEmpty()) {
                GroupStorrage.createGroup("default", true);
            }

            initHelpMessage();

            getCommand("bperms").setExecutor(new CMDBperms());

            getServer().getPluginManager().registerEvents(new JoinAndQuitListener(), this);
            if(useChat) getServer().getPluginManager().registerEvents(new ChatListener(), this);

            if(Bukkit.getOnlinePlayers().size() > 0) {
                System.out.println("[BPerms] There are already players online, was the server reloaded?");
                for(Player p : Bukkit.getOnlinePlayers()) {
                    UserManager.userConnected(p);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCfg() {
        getPlugin().saveConfig();
    }

    public void onDisable() {
        GroupStorrage.saveGroups();
        UserManager.saveUsers();
    }

    public static void sendHelpMessage(Player p) {
        for(TextComponent comp : help) {
            p.spigot().sendMessage(comp);
        }
    }

    public static BPerms getPlugin() { return plugin; }

    private void initHelpMessage() {
        help.add(new TextComponent("§b"));
        help.add(new TextComponent("§8§m                                                    §r"));
        help.add(new TextComponent("§b§b"));

        TextComponent BPerms = new TextComponent("                         §bBPerms §7- §bHelp");
        BPerms.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§b§lBPerms §7- §bCreated By Bethibande").create()));
        //BPerms.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bperms reload"));
        help.add(BPerms);

        help.add(new TextComponent("§b§c"));

        help.add(new TextComponent("\n §b§lGroup §7- §bCommands \n"));

        TextComponent reload = new TextComponent("§7 1. /bperms reload");
        reload.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Reload the plugin and mysql").create()));
        reload.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bperms reload"));
        help.add(reload);

        TextComponent groups = new TextComponent("§7 2. /bperms groups");
        groups.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Lists all groups").create()));
        groups.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bperms groups"));
        help.add(groups);

        TextComponent addgroup = new TextComponent("§7 3. /bperms addgroup [name]");
        addgroup.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Create a new group").create()));
        addgroup.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bperms addgroup [name]"));
        help.add(addgroup);

        TextComponent removegroup = new TextComponent("§7 4. /bperms removegroup [group]");
        removegroup.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Delete a group").create()));
        removegroup.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bperms removegroup [group]"));
        help.add(removegroup);

        TextComponent gInfo = new TextComponent("§7 5. /bperms group [group] info");
        gInfo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Shows more info about the specified group").create()));
        gInfo.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms group [group] info"));
        help.add(gInfo);

        TextComponent Import = new TextComponent("§7 6. /bperms group [group] import [group] <add/replace>");
        Import.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Copies all permissions a group has to another group").create()));
        Import.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms group [group] import [group] add/replace"));
        help.add(Import);

        TextComponent Implement = new TextComponent("§7 7. /bperms group [group] addParent [group]");
        Implement.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6The specified group will have all the permissions from its parent groups").create()));
        Implement.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms group [group] addParent [group]"));
        help.add(Implement);

        TextComponent UnImplement = new TextComponent("§7 8. /bperms group [group] removeParent [group]");
        UnImplement.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Remove a parent group from the specified group").create()));
        UnImplement.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms group [group] removeParent [group]"));
        help.add(UnImplement);

        TextComponent PermADD = new TextComponent("§7 9. /bperms group [group] permission add [permission]");
        PermADD.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Add a permission to a group").create()));
        PermADD.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms group [group] permission add [permission]"));
        help.add(PermADD);

        TextComponent PermREM = new TextComponent("§7 10. /bperms group [group] permission remove [permission]");
        PermREM.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Remove a permission from a group").create()));
        PermREM.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms group [group] permission remove [permission]"));
        help.add(PermREM);

        TextComponent Default = new TextComponent("§7 11. /bperms group [group] default <true/false>");
        Default.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6If true players will have this group when they join the server").create()));
        Default.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms group [group] default <true/false>"));
        help.add(Default);

        TextComponent Prefix = new TextComponent("§7 12. /bperms group [group] prefix [prefix]");
        Prefix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Define the prefix of a group").create()));
        Prefix.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms group [group] prefix [prefix]"));
        help.add(Prefix);

        TextComponent Suffix = new TextComponent("§7 13. /bperms group [group] suffix [suffix]");
        Suffix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Define the suffix of a group").create()));
        Suffix.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms group [group] suffix [suffix]"));
        help.add(Suffix);

        TextComponent Display = new TextComponent("§7 14. /bperms group [group] display [displayname]");
        Display.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Set the displayname of a group (nametag)").create()));
        Display.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms group [group] display [displayname]"));
        help.add(Display);

        TextComponent Tab = new TextComponent("§7 15. /bperms group [group] tab [tablistname]");
        Tab.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Set the tablist prefix of a group").create()));
        Tab.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms group [group] tab [tablistname]"));
        help.add(Tab);

        help.add(new TextComponent("\n §b§lPlayer §7- §bCommands \n"));

        TextComponent uInfo = new TextComponent("§7 16. /bperms user [player] info");
        uInfo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Shows more info of a player").create()));
        uInfo.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms user [player] info"));
        help.add(uInfo);

        TextComponent uAdd = new TextComponent("§7 17. /bperms user [player] group add [group]");
        uAdd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Add a group to a player").create()));
        uAdd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms user [player] group add [group]"));
        help.add(uAdd);

        TextComponent uRem = new TextComponent("§7 18. /bperms user [player] group remove [group]");
        uRem.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Remove a group from a player").create()));
        uRem.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms user [player] group remove [group]"));
        help.add(uRem);

        TextComponent uParent = new TextComponent("§7 19. /bperms user [player] parent set [group]");
        uParent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Set the main group of a player (for prefixes)").create()));
        uParent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms user [player] parent set [group]"));
        help.add(uParent);

        TextComponent permList = new TextComponent("§7 20. /bperms listPerms");
        permList.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Show all the permissions that are set as true for you at the moment").create()));
        permList.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bperms listPerms"));
        help.add(permList);

        help.add(new TextComponent("§b§b§b"));
        help.add(new TextComponent("§8§m                                                    §r"));
        help.add(new TextComponent("§b§b§b§b"));
    }

}
