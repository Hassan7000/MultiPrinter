package me.hassan.multiprinter.hook;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.brcdev.shopgui.ShopGuiPlusApi;

public class ShopGuiPlusHook {
	
	public static double getPricePerBlock(Player player, ItemStack item) {
		
		if(isSellable(player, item)) {
			return ShopGuiPlusApi.getItemStackPriceBuy(item);
		}
		
		return 0.0;
	}
	
	public static boolean isSellable(Player player, ItemStack item) {
		double sellPrice = ShopGuiPlusApi.getItemStackPriceBuy(player, item);
		 return (sellPrice > 0);
	}

}
