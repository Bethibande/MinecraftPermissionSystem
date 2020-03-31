package de.Bethibande.BPerms.Bungee;

import com.google.gson.Gson;
import de.Bethibande.BPerms.Groups.PermissionGroup;
import de.Bethibande.BPerms.Users.PermissionUser;
import de.Bethibande.BPerms.utils.MySQL;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserManager {

    public static List<PermissionUser> loadedUsers = new ArrayList<>();

    public static void userConnected(ProxiedPlayer p) {
        if(getPermissionUser(p) == null) {
            if (playerRegistered(p)) {
                loadedUsers.add(getUserFromSQL(p.getUniqueId()));
            } else register(p);
            loadPermissions(getPermissionUser(p));
        }
    }

    public static void userQuit(ProxiedPlayer p) {
        PermissionUser user = getPermissionUser(p);
        savePermissionUser(user);
        loadedUsers.remove(user);
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

    public static PermissionUser getPermissionUser(ProxiedPlayer p) {
        for(PermissionUser user : loadedUsers) {
            if(user.getUuid().equals(p.getUniqueId())) {
                return user;
            }
        }
        return null;
    }

    private static void register(ProxiedPlayer p) {
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
        for(ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
            PermissionUser user = getUserFromSQL(p.getUniqueId());
            loadPermissions(user);
        }
    }

    public static void loadPermissions(PermissionUser user) {
        ProxiedPlayer p = BungeeCord.getInstance().getPlayer(user.getUuid());
        List<String> remPerm = new ArrayList<>();
        p.getPermissions().forEach(remPerm::add);
        remPerm.forEach(perm -> p.setPermission(perm, false));

        if(p != null && p.isConnected()) {
            List<PermissionGroup> remove = new ArrayList<>();
            for(String pGroup : user.getGroups()) {
                PermissionGroup g = GroupStorrage.getGroup(pGroup);
                if(g != null) {
                    if(g.getPermissions() != null) {
                        for (String perm : g.getPermissions()) {
                            p.setPermission(perm, true);
                        }
                    }
                    if(g.getParents() != null) {
                        List<PermissionGroup> alreadyAdded = new ArrayList<>();
                        for(String par : g.getParents()) {
                            PermissionGroup parent = GroupStorrage.getGroup(par);
                            addPermissionsFromParent(parent, p, alreadyAdded);
                        }
                    }
                } else remove.add(g);
            }
            for(PermissionGroup g : remove) {
                user.getGroups().remove(g);
            }
            remove.clear();
        }
    }

    private static void addPermissionsFromParent(PermissionGroup group, ProxiedPlayer user, List<PermissionGroup> added) {

        if(!added.contains(group)) {
            added.add(group);
            if(group.getPermissions() != null) {
                for(String s : group.getPermissions()) {
                    user.setPermission(s, true);
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

    public static boolean playerRegistered(ProxiedPlayer p) {
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
