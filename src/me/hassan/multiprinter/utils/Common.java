package me.hassan.multiprinter.utils;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Common {
	
	public static void sendMessage(Player player, String message) {
		
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public static void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public static void executeConsoleCommand(String command) {
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
	}
	
	public static void executePlayerCommand(Player player, String command) {
		player.performCommand(command);
	}
	
	public static String colorMessage(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public static String formatValue(float value) {
	    String arr[] = {"", "K", "M", "B", "T", "P", "E"};
	    int index = 0;
	    while ((value / 1000) >= 1) {
	        value = value / 1000;
	        index++;
	    }
	    DecimalFormat decimalFormat = new DecimalFormat("#.##");
	    return String.format("%s %s", decimalFormat.format(value), arr[index]);
	}
	
	public static String formatNumbers(Double number){
        DecimalFormat dformater = new DecimalFormat("###,###,###,###.###");

        String formated = dformater.format(number);

        return formated;
    }

}
