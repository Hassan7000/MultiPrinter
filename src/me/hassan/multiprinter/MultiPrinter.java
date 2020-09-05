package me.hassan.multiprinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.hassan.multiprinter.command.PrinterCommand;
import me.hassan.multiprinter.evnets.PrinterBreak;
import me.hassan.multiprinter.evnets.PrinterDupe;
import me.hassan.multiprinter.evnets.PrinterPlace;
import me.hassan.multiprinter.utils.Common;
import net.milkbowl.vault.economy.Economy;


public class MultiPrinter extends JavaPlugin {
	
	private static MultiPrinter instance; 
	public ArrayList<UUID> printerState = new ArrayList<>();
	private HashMap<UUID, ItemStack[]> playerItem = new HashMap<>();
	private HashMap<UUID, ItemStack[]> armorItem = new HashMap<>();
	private HashMap<UUID, ArrayList<Location>> canBreak = new HashMap<>();
	private HashMap<UUID, Double> moneySpent = new HashMap<>();
	public HashMap<UUID, Location> teleportBack = new HashMap<>();
	public Economy econ = null;
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		setupEconomy();
		getCommand("printer").setExecutor(new PrinterCommand());
		Bukkit.getPluginManager().registerEvents(new PrinterPlace(), this);
		Bukkit.getPluginManager().registerEvents(new PrinterBreak(), this);
		Bukkit.getPluginManager().registerEvents(new PrinterDupe(), this);
	}
	
	public void onDisable() {
		for(Player player : Bukkit.getOnlinePlayers()){
			UUID uuid = player.getUniqueId();
			if(MultiPrinter.getInstance().getPrinterState().contains(uuid)) {
				MultiPrinter.getInstance().getPrinterState().remove(uuid);
				MultiPrinter.getInstance().printerState.remove(uuid);
				
				
				if(player.getGameMode() == GameMode.CREATIVE) {
					player.setGameMode(GameMode.SURVIVAL);
					
					if(MultiPrinter.getInstance().getPlayerItem().containsKey(uuid) && MultiPrinter.getInstance().getArmorItem().containsKey(uuid)) {
						player.getInventory().setContents(MultiPrinter.getInstance().getPlayerItem().get(uuid));
						player.getInventory().setArmorContents(MultiPrinter.getInstance().getArmorItem().get(uuid));
						
						MultiPrinter.getInstance().getArmorItem().remove(uuid);
						MultiPrinter.getInstance().getPlayerItem().remove(uuid);
						
						MultiPrinter.getInstance().getCanBreak().remove(uuid);
						
						if(MultiPrinter.getInstance().getMoneySpent().containsKey(uuid)) {
							double price = MultiPrinter.getInstance().getMoneySpent().get(uuid);
							
							
							MultiPrinter.getInstance().getMoneySpent().remove(uuid);
						}
					}
					
				}
			}
		}
		
		printerState.clear();
		playerItem.clear();
		armorItem.clear();
		canBreak.clear();
		moneySpent.clear();
		teleportBack.clear();
	}
	
	public static MultiPrinter getInstance() { return instance; }
	
	public ArrayList<UUID> getPrinterState() { return printerState; }
	
	public HashMap<UUID, ItemStack[]> getPlayerItem() { return playerItem; }
	
	public HashMap<UUID, ItemStack[]> getArmorItem() { return armorItem; }
	
	public HashMap<UUID, ArrayList<Location>> getCanBreak() { return canBreak; }
	
	public HashMap<UUID, Double> getMoneySpent() { return moneySpent; }
	
	public HashMap<ItemStack, Double> getSellList(){
		HashMap<ItemStack,Double> priceBlockList = new HashMap<>();
		for(String material : this.getConfig().getStringList("SellList")) {
			String[] split = material.split(":");
			String mat = split[0].toUpperCase();
			int data = Integer.valueOf(split[1]);
			double price = Double.valueOf(split[2]);
			ItemStack item = new ItemStack(Material.valueOf(mat), 1, (short) data);
			priceBlockList.put(item, price);
		}
		return priceBlockList;
	}
	
	@SuppressWarnings("unused")
	private boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return false;
		} else {
			RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
			if (rsp == null) {
				return false;
			} else {
				econ = (Economy) rsp.getProvider();
				return econ != null;
			}
		}
	}
	

}
