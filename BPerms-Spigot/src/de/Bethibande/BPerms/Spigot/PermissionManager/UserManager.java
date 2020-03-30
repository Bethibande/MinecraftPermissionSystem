package de.Bethibande.BPerms.Spigot.PermissionManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import de.Bethibande.BPerms.Groups.PermissionGroup;
import de.Bethibande.BPerms.Spigot.BPerms;
import de.Bethibande.BPerms.Spigot.PermissibleInjector;
import de.Bethibande.BPerms.Users.PermissionUser;
import de.Bethibande.BPerms.utils.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserManager {

    public static List<PermissionUser> loadedUsers = new ArrayList<>();
    public static HashMap<UUID, PermissionAttachment> permAttachments = new HashMap<>();

    public static void userConnected(Player p) {
        if(playerRegistered(p)) {
            loadedUsers.add(getUserFromSQL(p.getUniqueId()));
        } else register(p);
        PermissibleInjector.inject(p);
        loadPermissions(getPermissionUser(p));
    }

    public static void userQuit(Player p) {
        PermissionUser user = getPermissionUser(p);
        savePermissionUser(user);
        unloadPermissions(p);
        PermissibleInjector.uninject(p);
        loadedUsers.remove(user);
    }

    public static void unloadPermissions(Player p) {
        PermissionAttachment att = permAttachments.get(p.getUniqueId());
        p.removeAttachment(att);
    }

    public static PermissionUser getOfflinePlayer(String name) {
        try {
            ResultSet res = MySQL.INSTANCE.query("select * from bperms_users where Json Like '%\"username\":\"" + name + "\"%';");
            res.next();
            PermissionUser user = new Gson().fromJson(res.getString("Json"), PermissionUser.class);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PermissionUser getPermissionUser(Player p) {
        for(PermissionUser user : loadedUsers) {
            if(user.getUuid().equals(p.getUniqueId())) {
                return user;
            }
        }
        return null;
    }

    private static void register(Player p) {
        List<String> defaultGroups = new ArrayList<>();
        for(PermissionGroup g : GroupStorrage.getGroups()) {
            if(g.isDefault()) {
                defaultGroups.add(g.getName());
            }
        }
        PermissionUser user = new PermissionUser(p.getUniqueId(), p.getName(), defaultGroups, defaultGroups.get(0));
        loadedUsers.add(user);
        saveNewPermissionUser(user);
    }

    public static void updateAllPlayers() {
        loadedUsers.clear();
        for(Player p : Bukkit.getOnlinePlayers()) {
            PermissionUser user = getUserFromSQL(p.getUniqueId());
            loadPermissions(user);
        }
    }

    public static void loadPermissions(PermissionUser user) {
        Player p = Bukkit.getPlayer(user.getUuid());
        if(p != null && p.isOnline()) {
            if(permAttachments.containsKey(p.getUniqueId())) {
                PermissionAttachment old = permAttachments.get(p.getUniqueId());
                p.removeAttachment(old);
                permAttachments.remove(p.getUniqueId());
            }
            PermissionAttachment att = p.addAttachment(BPerms.getPlugin());
            permAttachments.put(p.getUniqueId(), att);
            List<PermissionGroup> remove = new ArrayList<>();
            for(String pGroup : user.getGroups()) {
                PermissionGroup g = GroupStorrage.getGroup(pGroup);
                if(g != null) {
                    if(g.getPermissions() != null) {
                        for (String perm : g.getPermissions()) {
                            att.setPermission(perm, true);
                        }
                    }
                    if(g.getParents() != null) {
                        List<PermissionGroup> alreadyAdded = new ArrayList<>();
                        for(String par : g.getParents()) {
                            PermissionGroup parent = GroupStorrage.getGroup(par);
                            addPermissionsFromParent(parent, user, alreadyAdded);
                        }
                    }
                } else remove.add(g);
            }
            for(PermissionGroup g : remove) {
                user.getGroups().remove(g);
            }
            remove.clear();
            p.recalculatePermissions();
        }
    }

    private static void addPermissionsFromParent(PermissionGroup group, PermissionUser user, List<PermissionGroup> added) {
        if(!added.contains(group)) {
            added.add(group);
            if(group.getPermissions() != null) {
                PermissionAttachment att = permAttachments.get(user.getUuid());
                for(String s : group.getPermissions()) {
                    att.setPermission(s, true);
                }
            }
            if(group.getParents() != null) {
                for(String g : group.getParents()) {
                    PermissionGroup parent = GroupStorrage.getGroup(g);
                    addPermissionsFromParent(parent, user, added);
                }
            }
        }
    }

    public static void saveUsers() {
        for(PermissionUser user : loadedUsers) {
            savePermissionUser(user);
        }
    }

    public static PermissionUser getUserFromSQL(UUID uuid) {
        try {
            ResultSet res = MySQL.INSTANCE.query("select * from bperms_users where PermissionUser='" + uuid + "';");
            if(!res.next()) return null;
            return new Gson().fromJson(res.getString("Json"), PermissionUser.class);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean playerRegistered(Player p) {
        try {
            ResultSet res = MySQL.INSTANCE.query("select * from bperms_users where PermissionUser='" + p .getUniqueId() + "';");
            return res.next();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveNewPermissionUser(PermissionUser user) {
        MySQL.INSTANCE.update("insert into bperms_users(PermissionUser, Json) values ('" + user.getUuid() + "', '" + new Gson().toJson(user) + "');");
    }
    public static void savePermissionUser(PermissionUser user) {
        MySQL.INSTANCE.update("update bperms_users set Json='" + new Gson().toJson(user) + "' where PermissionUser='" + user.getUuid() + "';");
    }

    public static void init() {
        MySQL mysql = MySQL.INSTANCE;

        mysql.update("create table if not exists bperms_users(PermissionUser VARCHAR(128), Json TEXT(65000), UNIQUE KEY(PermissionUser));");
    }

}
