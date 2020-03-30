package de.Bethibande.BPerms.Spigot;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.LinkedHashMap;

public class BPermissible extends PermissibleBase {

    private Player p;

    @Getter
    @Setter
    private PermissibleBase old;

    private LinkedHashMap<String, PermissionAttachmentInfo> attachments;

    public BPermissible(Player p) {
        super(p);
        this.p = p;
        this.attachments = new LinkedHashMap<String, PermissionAttachmentInfo>() {
            @Override
            public PermissionAttachmentInfo put(String k, PermissionAttachmentInfo v) {
                PermissionAttachmentInfo existing = this.get(k);
                if (existing != null) {
                    return existing;
                }

                return super.put(k, v);
            }
        };
    }



    @Override
    public boolean hasPermission(String permission) {
        for(PermissionAttachmentInfo i : super.getEffectivePermissions()) {
            String perm = i.getPermission();
            if(perm.endsWith("*")) {
                if(permission.startsWith(perm.substring(0, perm.length()-1))) {
                    return true;
                }
            }
            if(perm.equalsIgnoreCase(permission)) return true;
        }

        if(p.isOp()) return true;
        return false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {

        return super.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {

        return super.addAttachment(plugin, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {

        return super.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {

        return super.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {

        super.removeAttachment(attachment);
    }

}
