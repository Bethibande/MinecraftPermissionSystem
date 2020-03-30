package de.Bethibande.BPerms.Spigot.Commands;

import de.Bethibande.BPerms.Groups.PermissionGroup;
import de.Bethibande.BPerms.Spigot.BPerms;
import de.Bethibande.BPerms.Spigot.PermissionManager.GroupStorrage;
import de.Bethibande.BPerms.Spigot.PermissionManager.UserManager;
import de.Bethibande.BPerms.Users.PermissionUser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import javax.crypto.interfaces.PBEKey;
import java.util.ArrayList;

public class CMDBperms implements CommandExecutor {

    /* Command - Map
     *
     * /bperms addgroup [Name]
     * /bperms removegroup [Gruppe]
     *
     * /bperms group [Gruppe] info
     * /bperms group [Gruppe] import [Gruppe] <add/copy>
     * /bperms group [Gruppe] permission add [permission]
     * /bperms group [Gruppe] permission remove [permission]
     * /bperms group [Gruppe] parent [Gruppe]
     * /bperms group [Gruppe] unparent [Gruppe]
     *   placeholder - "_" = " "
     *
     * /bperms group [Gruppe] prefix [prefix]
     * /bperms group [Gruppe] suffix [suffix]
     *
     *  User commands
     * /bperms user [User] info
     * /bperms user [User] group add [Gruppe]
     * /bperms user [User] group remove [Gruppe]
     * /bperms user [User] parent set [Group]
     *
     */

    public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {

        if(sender instanceof Player) {
            Player p = (Player)sender;

            if (p.hasPermission(BPerms.bperms_command_permission)) {

                if(args.length == 0) {
                    p.sendMessage(BPerms.prefix + "execute §6/bperms help§7 for help.");
                    return true;
                }
                if(args.length == 1 && args[0].equalsIgnoreCase("help")) {
                    BPerms.sendHelpMessage(p);
                    return true;
                }

                if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    GroupStorrage.reloadFromSQL();
                    UserManager.updateAllPlayers();
                    p.sendMessage(BPerms.prefix + "Plugin reloaded!");
                    return true;
                }

                if(args.length == 1 && args[0].equalsIgnoreCase("groups")) {
                    p.sendMessage(BPerms.prefix + " All groups:");
                    for(PermissionGroup g : GroupStorrage.getGroups()) {
                        p.sendMessage("  §7" + g.getName());
                    }
                    return true;
                }

                if(args.length == 1 && args[0].equalsIgnoreCase("listperms")) {
                    PermissionAttachment att = UserManager.permAttachments.get(p.getUniqueId());
                    p.sendMessage("§6");
                    if(att != null && att.getPermissions() != null) {
                        p.sendMessage(BPerms.prefix + "Currently active permissions for you§8:");
                        for (String s : att.getPermissions().keySet()) {
                            if (att.getPermissions().get(s)) {
                                p.sendMessage("  §7" + s);
                            }
                        }
                    } else p.sendMessage(BPerms.prefix + "Currently active permissions for you§8: §7-");
                    p.sendMessage("§6");
                    return true;
                }

                if(args.length == 2 && args[0].equalsIgnoreCase("addgroup")) {
                    if(!GroupStorrage.groupExists(args[1])) {
                        GroupStorrage.createGroup(args[1]);
                        p.sendMessage(BPerms.prefix + "Group created!");
                        return true;
                    }
                    p.sendMessage(BPerms.prefix + "A group with this name already exists!");
                    return true;
                }

                if(args.length == 2 && args[0].equalsIgnoreCase("removegroup")) {
                    if(GroupStorrage.groupExists(args[1])) {
                        GroupStorrage.deletedGroup(GroupStorrage.getGroup(args[1]));
                        p.sendMessage(BPerms.prefix + "Group deleted!");
                        return true;
                    }
                    p.sendMessage(BPerms.prefix + "There is no group with this name!");
                    return true;
                }

                // group commands
                if(args.length >= 3 && args[0].equalsIgnoreCase("group")) {
                    String group = args[1];
                    PermissionGroup g = GroupStorrage.getGroup(group);

                    if(g != null) {

                        if(args.length == 3 && args[2].equalsIgnoreCase("info")) {
                            p.sendMessage("§6");
                            p.sendMessage("          §b§lInfo §8- §b" + g.getName());
                            p.sendMessage("§6");
                            if(g.isDefault()) p.sendMessage("  §bis default§8: §aYes");
                            if(!g.isDefault()) p.sendMessage("  §bis default§8: §cNo");
                            if(g.getPermissions() == null) p.sendMessage("  §bPermissions§8: §7-");
                            if(g.getPermissions() != null) {
                                p.sendMessage("  §bPermissions§8:");
                                for(String s : g.getPermissions()) {
                                    p.sendMessage("    §7" + s);
                                }
                            }
                            if(g.getParents() != null) {
                                p.sendMessage("  §bParent groups§8:");
                                for(String s : g.getParents()) {
                                    p.sendMessage("    §7" + s);
                                }
                            }
                            if(g.getParents() == null) p.sendMessage("  §bParent groups§8: §7-");
                            if(g.getPrefix() != null) p.sendMessage("  §bPrefix§8: §f" + g.getPrefix());
                            if(g.getPrefix() == null) p.sendMessage("  §bPrefix§8: §7-");
                            if(g.getSuffix() != null) p.sendMessage("  §bSuffix§8: §f" + g.getSuffix());
                            if(g.getSuffix() == null) p.sendMessage("  §bSuffix§8: §7-");
                            if(g.getTablist() != null) p.sendMessage("  §bTablist§8: §f" + g.getTablist());
                            if(g.getTablist() == null) p.sendMessage("  §bTablist§8: §7-");
                            if(g.getDisplay() != null) p.sendMessage("  §bDisplayname§8: §f" + g.getDisplay());
                            if(g.getDisplay() == null) p.sendMessage("  §bDisplayname§8: §7-");
                            p.sendMessage(" §6");
                            return true;
                        }

                        if(args.length == 4 && args[2].equalsIgnoreCase("default")) {
                            boolean b = new Boolean(args[3]);
                            g.setDefault(b);
                            GroupStorrage.saveGroup(g);
                            p.sendMessage(BPerms.prefix + "Set default to " + b + "!");
                            return true;
                        }

                        if(args.length == 5 && args[2].equalsIgnoreCase("permission")) {
                            if(args[3].equalsIgnoreCase("add")) {
                                if(g.getPermissions() == null) g.setPermissions(new ArrayList<>());
                                if(!g.getPermissions().contains(args[4])) g.getPermissions().add(args[4]);
                                GroupStorrage.saveGroup(g);
                                p.sendMessage(BPerms.prefix + "Permission added successfully!");
                                return true;
                            }
                            if(args[3].equalsIgnoreCase("remove")) {
                                if(g.getPermissions() == null) g.setPermissions(new ArrayList<>());
                                if(g.getPermissions().contains(args[4])) g.getPermissions().remove(args[4]);
                                GroupStorrage.saveGroup(g);
                                p.sendMessage(BPerms.prefix + "Permission remove successfully!");
                                return true;
                            }
                            p.sendMessage(BPerms.prefix + "execute §6/bperms help§7 for help.");
                            return true;
                        }

                        if(args.length == 4 && args[2].equalsIgnoreCase("addparent")) {
                            PermissionGroup g2 = GroupStorrage.getGroup(args[3]);
                            if(g2 != null) {
                                if(g.getParents() == null) g.setParents(new ArrayList<>());
                                if(!g.getParents().contains(g2.getName())) g.getParents().add(g2.getName());
                                GroupStorrage.saveGroup(g);
                                p.sendMessage(BPerms.prefix + "Parent added successfully!");
                                return true;
                            }
                            p.sendMessage(BPerms.prefix + "One of the specified groups were not found");
                            return true;
                        }

                        if(args.length == 4 && args[2].equalsIgnoreCase("removeparent")) {
                            PermissionGroup g2 = GroupStorrage.getGroup(args[3]);
                            if(g2 != null) {
                                if(g.getParents() == null) g.setParents(new ArrayList<>());
                                if(g.getParents().contains(g2.getName())) g.getParents().remove(g2.getName());
                                GroupStorrage.saveGroup(g);
                                p.sendMessage(BPerms.prefix + "Parent removed successfully!");
                                return true;
                            }
                            p.sendMessage(BPerms.prefix + "One of the specified groups were not found");
                            return true;
                        }

                        if(args.length == 4 && args[2].equalsIgnoreCase("prefix")) {
                            g.setPrefix(args[3].replaceAll("&", "§").replaceAll("_", " "));
                            GroupStorrage.saveGroup(g);
                            p.sendMessage(BPerms.prefix +  "Prefix set!");
                            return true;
                        }

                        if(args.length == 4 && args[2].equalsIgnoreCase("suffix")) {
                            g.setSuffix(args[3].replaceAll("&", "§").replaceAll("_", " "));
                            GroupStorrage.saveGroup(g);
                            p.sendMessage(BPerms.prefix +  "Suffix set!");
                            return true;
                        }

                        if(args.length == 4 && args[2].equalsIgnoreCase("display")) {
                            g.setDisplay(args[3].replaceAll("&", "§").replaceAll("_", " "));
                            GroupStorrage.saveGroup(g);
                            p.sendMessage(BPerms.prefix +  "Displayname set!");
                            return true;
                        }

                        if(args.length == 4 && args[2].equalsIgnoreCase("tab")) {
                            g.setTablist(args[3].replaceAll("&", "§").replaceAll("_", " "));
                            GroupStorrage.saveGroup(g);
                            p.sendMessage(BPerms.prefix +  "Tablist prefix set!");
                            return true;
                        }

                        if(args.length == 5 && args[2].equalsIgnoreCase("import")) {
                            String t = args[3];
                            PermissionGroup g2 = GroupStorrage.getGroup(t);
                            if(g2 != null) {
                                if(g.getPermissions() == null) g.setPermissions(new ArrayList<>());
                                if(args[4].equalsIgnoreCase("add")) {
                                    if(g2.getPermissions() != null) {
                                        for(String perm : g2.getPermissions()) {
                                            g.getPermissions().add(perm);
                                        }
                                    }
                                    GroupStorrage.saveGroup(g);
                                    p.sendMessage(BPerms.prefix + "Permissions added successfully!");
                                    return true;
                                }

                                if(args[4].equalsIgnoreCase("replace")) {
                                    g.getPermissions().clear();
                                    if(g2.getPermissions() != null) {
                                        for(String perm : g2.getPermissions()) {
                                            g.getPermissions().add(perm);
                                        }
                                    }
                                    GroupStorrage.saveGroup(g);
                                    p.sendMessage(BPerms.prefix + "Permissions replaced successfully!");
                                    return true;
                                }
                                p.sendMessage(BPerms.prefix + "execute §6/bperms help§7 for help.");
                                return true;
                            }
                            p.sendMessage(BPerms.prefix + "One of the specified groups were not found!");
                            return true;
                        }

                        p.sendMessage(BPerms.prefix + "execute §6/bperms help§7 for help.");
                        return true;
                    } else {
                        p.sendMessage(BPerms.prefix + "The specified group doesn't exist!");
                        return true;
                    }

                }

                if(args.length >= 3 && args[0].equalsIgnoreCase("user")) {
                    Player t = Bukkit.getPlayer(args[1]);
                    PermissionUser user = null;
                    if(t != null && t.isOnline()) user = UserManager.getPermissionUser(t);
                    if(t == null || !t.isOnline()) user = UserManager.getOfflinePlayer(args[1]);
                    if(user == null) {
                        p.sendMessage(BPerms.prefix + "The specified user was not found!");
                        return true;
                    }
                    if(args.length == 3 && args[2].equalsIgnoreCase("info")) {
                        p.sendMessage("§6");
                        p.sendMessage("          §b§lInfo §8- §b" + user.getUsername());
                        p.sendMessage("§6");
                        p.sendMessage("  §bUUID§8: §7" + user.getUuid());
                        p.sendMessage("  §bParent group§8: §7" + user.getParent());
                        p.sendMessage("  §bGroups§8: §7");
                        for(String group : user.getGroups()) {
                            p.sendMessage("    §7" + group);
                        }
                        p.sendMessage("§6");
                        return true;
                    }

                    if(args.length == 5 && args[2].equalsIgnoreCase("group")) {
                        PermissionGroup g = GroupStorrage.getGroup(args[4]);
                        if(g == null) {
                            p.sendMessage(BPerms.prefix + "The specified group wasn't found!");
                            return true;
                        }
                        if(args[3].equalsIgnoreCase("add")) {
                            if(!user.getGroups().contains(g.getName())) {
                                user.getGroups().add(g.getName());
                                UserManager.savePermissionUser(user);
                                p.sendMessage(BPerms.prefix + "Group added!");
                            } else p.sendMessage(BPerms.prefix + "The user already has the specified group!");
                            return true;
                        }

                        if(args[3].equalsIgnoreCase("remove")) {
                            if(user.getGroups().contains(g.getName())) {
                                user.getGroups().remove(g.getName());
                                UserManager.savePermissionUser(user);
                                p.sendMessage(BPerms.prefix + "Group removed!");
                            } else p.sendMessage(BPerms.prefix + "The user doesn't have the specified group!");
                            return true;
                        }

                        p.sendMessage(BPerms.prefix + "execute §6/bperms help§7 for help.");
                        return true;
                    }

                    if(args.length == 5 && args[2].equalsIgnoreCase("parent") && args[3].equalsIgnoreCase("set")) {
                        PermissionGroup g = GroupStorrage.getGroup(args[4]);
                        if(g == null) {
                            p.sendMessage(BPerms.prefix + "The specified group wasn't found!");
                            return true;
                        }

                        user.setParent(g.getName());
                        if(!user.getGroups().contains(g.getName())) user.getGroups().add(g.getName());
                        UserManager.savePermissionUser(user);
                        p.sendMessage(BPerms.prefix + "Parent set!");

                        return true;
                    }

                    p.sendMessage(BPerms.prefix + "execute §6/bperms help§7 for help.");
                    return true;
                }

                p.sendMessage(BPerms.prefix + "execute §6/bperms help§7 for help.");

            } else p.sendMessage(BPerms.prefix + "You are not permitted to use this command!");

        }

        return false;
    }

}
