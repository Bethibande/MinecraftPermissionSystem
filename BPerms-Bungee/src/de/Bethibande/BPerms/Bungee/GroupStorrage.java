package de.Bethibande.BPerms.Bungee;

import com.google.gson.Gson;
import de.Bethibande.BPerms.Groups.PermissionGroup;
import de.Bethibande.BPerms.utils.MySQL;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupStorrage {

    @Getter
    private static List<PermissionGroup> groups = new ArrayList<>();

    public static void loadGroups() {
        try {
            ResultSet res = MySQL.INSTANCE.query("select * from bperms_groups");
            while(res.next()) {
                String Json = res.getString("Json");
                groups.add(new Gson().fromJson(Json, PermissionGroup.class));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveGroups() {
        for(PermissionGroup g : groups) {
            saveGroup(g);
        }
    }

    public static void createGroup(String name) {
        PermissionGroup g = new PermissionGroup(name, false);
        groups.add(g);
        MySQL.INSTANCE.update("insert into bperms_groups(PermissionGroup, Json) values ('" + g.getName() + "', '" + new Gson().toJson(g) + "');");
    }

    public static void createGroup(String name, boolean isDefault) {
        PermissionGroup g = new PermissionGroup(name, isDefault);
        groups.add(g);
        MySQL.INSTANCE.update("insert into bperms_groups(PermissionGroup, Json) values ('" + g.getName() + "', '" + new Gson().toJson(g) + "');");
    }

    public static void deletedGroup(PermissionGroup g) {
        MySQL.INSTANCE.update("delete from bperms_groups where PermissionGroup='" + g.getName() + "';");
        groups.remove(g);
    }

    public static List<PermissionGroup> listGroupsFromMySQL() {
        try {
            List<PermissionGroup> groups = new ArrayList<>();
            ResultSet res = MySQL.INSTANCE.query("select * from bperms_groups");
            while(res.next()) {
                groups.add(new Gson().fromJson(res.getString("Json"), PermissionGroup.class));
            }
            return groups;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PermissionGroup getGroup(String name) {
        for(PermissionGroup g : groups) {
            if(g.getName().equalsIgnoreCase(name)) return g;
        }
        return null;
    }

    public static void reloadFromSQL() {
        groups.clear();
        groups = listGroupsFromMySQL();
    }

    public static void saveGroup(PermissionGroup g) {
        MySQL.INSTANCE.update("update bperms_groups set Json='" + new Gson().toJson(g) + "' where PermissionGroup='" + g.getName() + "';");
    }

    public static boolean groupExists(String name) {
        return getGroup(name) != null;
    }

    public static void init() {
        MySQL mysql = MySQL.INSTANCE;

        mysql.update("create table if not exists bperms_groups(PermissionGroup VARCHAR(128), Json TEXT(65000), UNIQUE KEY(PermissionGroup));");
    }

}
