package me.hassan.multiprinter.printerhandler;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.hassan.multiprinter.MultiPrinter;
import me.hassan.multiprinter.hook.ShopGuiPlusHook;
import me.hassan.multiprinter.utils.Common;

public class PrinterHandler {
	
	
	public static boolean handleBlockPlaceMent(Player player, ItemStack item) {
		
		if(MultiPrinter.getInstance().getConfig().getBoolean("Settings.ShopGuiPlus")) {
			
			if(ShopGuiPlusHook.getPricePerBlock(player, item) > 0) {
				
				double balance = MultiPrinter.getInstance().econ.getBalance(player);
				double price = ShopGuiPlusHook.getPricePerBlock(player, item);
				UUID uuid = player.getUniqueId();
				if(balance >= price) {
					
					MultiPrinter.getInstance().econ.withdrawPlayer(player, price);
					
					if(MultiPrinter.getInstance().getMoneySpent().containsKey(uuid)) {
						
						MultiPrinter.getInstance().getMoneySpent().put(uuid, MultiPrinter.getInstance().getMoneySpent().get(uuid) + price);
					}else {
						MultiPrinter.getInstance().getMoneySpent().put(uuid, price);
					}
					
					return true;
					
				}else {
					Common.sendMessage(player, "&cYou don't have enough money to place this block");
					return false;
				}
				
				
			}else {
				Common.sendMessage(player, "&cBlock is not sellable");
				return false;
			}
		}else {
			
			if(MultiPrinter.getInstance().getSellList().containsKey(item)) {
				
				double balance = MultiPrinter.getInstance().econ.getBalance(player);
				double price = MultiPrinter.getInstance().getSellList().get(item);
				UUID uuid = player.getUniqueId();
				if(balance >= price) {
					MultiPrinter.getInstance().econ.withdrawPlayer(player, price);
					
					
					if(MultiPrinter.getInstance().getMoneySpent().containsKey(uuid)) {
						
						MultiPrinter.getInstance().getMoneySpent().put(uuid, MultiPrinter.getInstance().getMoneySpent().get(uuid) + price);
					}else {
						MultiPrinter.getInstance().getMoneySpent().put(uuid, price);
					}
					
					return true;
				}else {
					Common.sendMessage(player, "&cYou don't have enough money to place this block");
					return false;
				}
			}else {
				Common.sendMessage(player, "&cBlock is not sellable");
				return false;
			}
		}
	}
	
	public static boolean isSellabe(Player player, ItemStack item) {
		if(MultiPrinter.getInstance().getConfig().getBoolean("Settings.ShopGuiPlus")) {
			return ShopGuiPlusHook.isSellable(player, item);
		}
		return false;
	}

}
