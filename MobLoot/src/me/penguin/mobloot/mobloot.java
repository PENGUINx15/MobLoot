package me.penguin.mobloot;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class mobloot extends JavaPlugin implements Listener {


    private MobLootConfig mobLootConfig;
    
    private DropEggListener dropEggListener;
    private CurrencyDropsListener currencyDropsListener;
    private CommandEntitiesListener commandEntitiesListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        registerListeners();
    }
    
    private void loadConfig() {
        mobLootConfig = new MobLootConfig(getConfig());
    }
    
    private void registerListeners() {
    	currencyDropsListener = new CurrencyDropsListener(this, mobLootConfig);
        Bukkit.getPluginManager().registerEvents(currencyDropsListener, this);
        
        dropEggListener = new DropEggListener(this, mobLootConfig);
        Bukkit.getPluginManager().registerEvents(dropEggListener, this);
        
        commandEntitiesListener = new CommandEntitiesListener(this, mobLootConfig);
        Bukkit.getPluginManager().registerEvents(commandEntitiesListener, this);
    }
    

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mobloot")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("mobloot.reload")) {
                	
                	Bukkit.getPluginManager().disablePlugin(this);
                    Bukkit.getPluginManager().enablePlugin(this);
                    
                    sender.sendMessage(mobLootConfig.getConfigReloadMessage().replace("&", "§"));
                    return true;
                } else {
                    sender.sendMessage(mobLootConfig.getNoPermissionMessage().replace("&", "§"));
                    return true;
                }
            }
        }
        return false;
    }
}

