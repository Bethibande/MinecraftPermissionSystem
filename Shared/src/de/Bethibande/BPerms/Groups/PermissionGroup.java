package de.Bethibande.BPerms.Groups;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PermissionGroup {

    @Getter
    private String name;
    @Getter
    @Setter
    private List<String> permissions;
    @Getter
    @Setter
    private String prefix;
    @Getter
    @Setter
    private String suffix;
    @Getter
    @Setter
    private String tablist;
    @Getter
    @Setter
    private String display;
    @Getter
    @Setter
    private boolean isDefault;
    @Getter
    @Setter
    private List<String> parents;

    public PermissionGroup(String name, boolean isDefault) {
        this.name = name;
        this.isDefault = isDefault;
    }

}
