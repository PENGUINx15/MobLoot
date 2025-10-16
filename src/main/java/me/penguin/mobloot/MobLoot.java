
package me.penguin.mobloot;

import me.penguin.mobloot.API.ConfigManager;
import me.penguin.mobloot.API.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public class MobLoot extends JavaPlugin implements Listener {

    private MessageManager messageManager;
    private ConfigManager config;

    @Override
    public void onEnable() {
        config = new ConfigManager(this);
        config.registerConfig("config.yml");

        CurrencyDropsListener currencyDropsListener = new CurrencyDropsListener(this, config, messageManager);
        Bukkit.getPluginManager().registerEvents(currencyDropsListener, this);

        messageManager = new MessageManager(this);
        Bukkit.getPluginManager().registerEvents(messageManager, this);

        DropEggListener dropEggListener = new DropEggListener(config, messageManager);
        Bukkit.getPluginManager().registerEvents(dropEggListener, this);

        CommandEntitiesListener commandEntitiesListener = new CommandEntitiesListener(config, messageManager);
        Bukkit.getPluginManager().registerEvents(commandEntitiesListener, this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mobloot")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("mobloot.reload")) {

                    Bukkit.getPluginManager().disablePlugin(this);
                    Bukkit.getPluginManager().enablePlugin(this);

                    if (sender instanceof Player player) {
                        messageManager.sendMessage(player, config.getString("config.yml","ConfigReloadMsg"));
                    }
                    return true;
                } else {
                    if (sender instanceof Player player) {
                        messageManager.sendMessage(player, config.getString("config.yml", "NotPermMsg"));
                    }
                    return true;
                }
            }
        }
        return false;
    }
}

