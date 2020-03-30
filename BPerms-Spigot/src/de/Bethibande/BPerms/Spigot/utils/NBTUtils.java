package de.Bethibande.BPerms.Spigot.utils;

import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.PlayerInventory;

public class NBTUtils {

	public static boolean hasNBT(ItemStack i) {
		
		net.minecraft.server.v1_15_R1.ItemStack item = CraftItemStack.asNMSCopy(i);
		return item.getTag() != null;
	}
	
	public static String getValue(ItemStack i, String key) {
		String value = "";
		
		net.minecraft.server.v1_15_R1.ItemStack item = CraftItemStack.asNMSCopy(i);
		NBTTagCompound com = item.getTag();
		value = com.getString(key);
		
		return value;
	}
	
    public static String getTag(int slot, Player p, String key) {
        PlayerInventory inv = ((CraftPlayer)p).getHandle().inventory;
        NBTTagCompound com = inv.getItem(slot).getTag();
        if (com!=null && com.hasKey(key)) {
            return com.getString(key);
        } else {
            return null;
        }
    }
	
	public static boolean hasTag(ItemStack i, String key) {
		net.minecraft.server.v1_15_R1.ItemStack item = CraftItemStack.asNMSCopy(i);
		NBTTagCompound com = item.getTag();
		return com.hasKey(key);
	}

	public static ItemStack setValue(String key, String value, ItemStack item) {
		net.minecraft.server.v1_15_R1.ItemStack s = CraftItemStack.asNMSCopy(item);
		NBTTagCompound comp = s.getTag();
		comp.setString(key, value);
		return CraftItemStack.asBukkitCopy(s);
	}
	
}
