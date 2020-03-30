package de.Bethibande.BPerms.Users;

import de.Bethibande.BPerms.Groups.PermissionGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

public class PermissionUser {

    @Getter
    @Setter
    private UUID uuid;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private List<String> groups;
    @Getter
    @Setter
    private String parent;

    public PermissionUser(UUID uuid, String username, List<String> groups, String parent) {
        this.uuid = uuid;
        this.username = username;
        this.groups = groups;
        this.parent = parent;
    }

}
