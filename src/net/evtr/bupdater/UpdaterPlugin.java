package net.evtr.bupdater;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class UpdaterPlugin extends JavaPlugin {
	Download download;
	Logger log = Logger.getLogger("Minecraft");
	
	private class BukkitDownloader extends Download {
		public void onProgress(String str) {
			log.log(Level.INFO, "Downloader - " + str);
		}
		public void onFinish() {
			if ( hasError() ) {
				log.log(Level.WARNING, "Downloader - Error from \"" + getAddress() + "\" to \"" + getOutAddress() + "\": " + getMessage());
			} else {
				log.log(Level.INFO, "Downloader - \"" + getMessage() + "\"");
			}
			download = null;
		}
		public BukkitDownloader(String address, String output) {
			super(address, output);
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		String command = cmd.getName();
		
		if ( command.equalsIgnoreCase("update") ) {
			if ( sender.isOp() ) {
				if ( download == null ) {
					if ( args.length > 0 ) {
						String addr = args[0];
						String out = args.length > 1 ? args[1] : "";
						
						sender.sendMessage("Beginning download from " + addr);
						download = new BukkitDownloader("http://" + addr, "toupdate/" + out);
						download.Begin();
					} else {
						sender.sendMessage(ChatColor.RED + "No active downloads.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Download in progress.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Only ops have access to that command.");
			}
			
			return true;
		}
		return false;
	}
	public UpdaterPlugin() {
		download = null;
	}
}
