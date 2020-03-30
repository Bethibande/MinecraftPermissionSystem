package de.Bethibande.BPerms.Spigot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;

import java.lang.reflect.Field;
import java.util.List;

public class PermissibleInjector {

    private static Field permField;

    static {
        try {
            permField = Class.forName(getVersionedClassName("entity.CraftHumanEntity")).getDeclaredField("perm");
            permField.setAccessible(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void inject(Player p) {
        try {
            PermissibleBase old = (PermissibleBase)permField.get(p);
            BPermissible bPermissible = new BPermissible(p);

            Field attachments = PermissibleBase.class.getDeclaredField("attachments");
            attachments.setAccessible(true);
            ((List) attachments.get(bPermissible)).addAll((List)attachments.get(old));

            bPermissible.setOld(old);
            permField.set(p, bPermissible);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void uninject(Player p) {
        try {
            BPermissible bp = (BPermissible)permField.get(p);
            Permissible newP = new PermissibleBase(p);

            Field attachments = PermissibleBase.class.getDeclaredField("attachments");
            attachments.setAccessible(true);
            ((List) attachments.get(newP)).addAll((List)attachments.get(bp));

            permField.set(p, newP);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static String getVersionedClassName(String classname) {
        String version;

        Class serverClass = Bukkit.getServer().getClass();
        if (!serverClass.getSimpleName().equals("CraftServer")) {
            return null;
        }
        else if (serverClass.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
            version = ".";
        } else {
            version = serverClass.getName().substring("org.bukkit.craftbukkit".length());
            version = version.substring(0, version.length() - "CraftServer".length());
        }

        return "org.bukkit.craftbukkit" + version + classname;
    }

}
