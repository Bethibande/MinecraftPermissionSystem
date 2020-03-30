package de.Bethibande.BPerms.Spigot.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_15_R1.Block;
import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class ItemFactory {

	private Material mat;
	private int amount = 1;
	private String displayName = "";
	private int data = -1;
	
	private boolean hideEnchants = false;
	
	private HashMap<Enchantment, Integer> enchs = new HashMap<>();
	private List<String> lore = new ArrayList<String>(); 
	private HashMap<String, String> nbt = new HashMap<String, String>();
	
	public ItemFactory(Material mat) {
		this.mat = mat;
	}
	public ItemFactory(Material mat, String name) {
		this.mat = mat;
		this.displayName = name;
	}
	public ItemFactory(Material mat, String name, int amount) {
		this.mat = mat;
		this.displayName =name;
		this.amount = amount;
	}
	public ItemFactory(Material mat, int amount) {
		this.mat = mat;
		this.amount = amount;
	}
	
	public void addEnchant(Enchantment ench, int lvl) { this.enchs.put(ench, lvl); }
	public void hideEnchants(boolean b) { this.hideEnchants = b; }
	
	public void setName(String name) { this.displayName = name; }
	
	public void setLore(String... lore) {
		List<String> l = new ArrayList<String>();
		for(String s : lore) {
			l.add(s);
		}
		
		this.lore = l;
	}
	public void setNBT(String key, String value) {
		this.nbt.put(key, value);
	}
	public void remNBT(String key) {
		this.nbt.remove(key);
	}
	public void setData(int i) {
		this.data = i;
	}
	
	public ItemStack build() {
		ItemStack i = new ItemStack(this.mat, this.amount);
		
		/*if(!this.nbt.isEmpty()) {
			net.minecraft.server.v1_8_R3.ItemStack item = new net.minecraft.server.v1_8_R3.ItemStack(Block.getByName("STONE"));
			NBTTagCompound com = new NBTTagCompound();
			
			for(String key : this.nbt.keySet()) {
				String value = this.nbt.get(key);
				
				com.setString(key, value);
				
			}
			
			item.setTag(com);
			i = CraftItemStack.asBukkitCopy(item);
		}*/
		
		if(data != -1) {
			i.setDurability((short)data);
		}
		
		i.setType(this.mat);
		i.setAmount(this.amount);
		
		ItemMeta m = i.getItemMeta();
		m.setDisplayName(this.displayName);
		if(!this.lore.isEmpty()) m.setLore(this.lore);
		
		if(this.hideEnchants) { m.addItemFlags(ItemFlag.HIDE_ENCHANTS); }
		
		i.setItemMeta(m);
		
		if(!this.enchs.isEmpty()) {
			for(Enchantment e : this.enchs.keySet()) {
				i.addEnchantment(e, this.enchs.get(e));
			}
		}
		
		
		if(!this.nbt.isEmpty()) {
			net.minecraft.server.v1_15_R1.ItemStack s = CraftItemStack.asNMSCopy(i);
			NBTTagCompound comp = s.getTag();
			for(String key : this.nbt.keySet()) {
				String v = this.nbt.get(key);
				comp.setString(key, v);
			}
			s.setTag(comp);
			i = CraftItemStack.asBukkitCopy(s);
		}
		
		return i;
	}
	
	public static void clone(ItemStack item) {
		ItemMeta m = item.getItemMeta();
		ItemFactory i = new ItemFactory(item.getType());
		
		if(m.getDisplayName() != null) {
			i.setName(m.getDisplayName());
		}
		if(!m.getItemFlags().isEmpty()) {
			if(m.getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
				i.hideEnchants(true);
			}
		}
		if(m.hasEnchants()) {
			for(Enchantment ench : m.getEnchants().keySet()) {
				i.addEnchant(ench, m.getEnchantLevel(ench));
			}
		}
		if(m.hasLore()) {
			i.setLore(m.getLore().toArray(new String[0]));
		}
		
	}
	
}
