package me.penguin.mobloot;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class mobloot extends JavaPlugin implements Listener {

    private double eggDropChance;
    private boolean dropEggEnable;
    private double currencyDropChance;
    private int CurrencyDropsMin;
    private int CurrencyDropsMax;
    private String configReloadMessage;
    private String noPermissionMessage;
    private Map<Integer, EntityType> commandEntities;
    private List<EntityType> blacklistedDropEgg;
    private List<EntityType> blacklistedСurrencyDrop;
    private String getCurrencyMessage(EntityType entityType, int amount) {
        String message = getConfig().getString("CurrencyDrop.Msg");
        message = message.replace("{entity}", getFriendlyName(entityType));
        message = message.replace("{amount}", String.valueOf(amount));
        return message;
    }
    private String getFriendlyName(EntityType entityType) {
        return WordUtils.capitalizeFully(entityType.name().replace("_", " "));
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadConfigValues() {
        eggDropChance = getConfig().getDouble("DropEgg.Chance", 0.1);
        dropEggEnable = getConfig().getBoolean("DropEgg.Enable", true);
        currencyDropChance = getConfig().getDouble("CurrencyDrop.Chance", 0.1);
        CurrencyDropsMin = getConfig().getInt("CurrencyDrop.MinAmount", 10);
        CurrencyDropsMax = getConfig().getInt("CurrencyDrop.MaxAmount", 100);
        configReloadMessage = getConfig().getString("ConfigReloadMsg", "§aConfig reloaded");
        noPermissionMessage = getConfig().getString("NotPermMsg", "§cYou do not have permission.");

        blacklistedDropEgg = new ArrayList<>();
        List<String> blacklist = getConfig().getStringList("DropEgg.Blacklist");
        for (String entityName : blacklist) {
            EntityType entityType = EntityType.fromName(entityName);
            if (entityType != null) {
                blacklistedDropEgg.add(entityType);
            } else {
                getLogger().warning("Invalid entity type in config: " + entityName);
            }
        }
        
        blacklistedСurrencyDrop = new ArrayList<>();
        List<String> blacklistq = getConfig().getStringList("CurrencyDrop.Blacklist");
        for (String entityName : blacklist) {
            EntityType entityType = EntityType.fromName(entityName);
            if (entityType != null) {
            	blacklistedСurrencyDrop.add(entityType);
            } else {
                getLogger().warning("Invalid entity type in config: " + entityName);
            }
        }
        
        commandEntities = new HashMap<>();
        ConfigurationSection entitySection = getConfig().getConfigurationSection("Entity");
        if (entitySection != null) {
            for (String key : entitySection.getKeys(false)) {
                ConfigurationSection entityConfig = entitySection.getConfigurationSection(key);
                if (entityConfig != null) {
                    int entityId = Integer.parseInt(key);
                    String entityName = entityConfig.getString("EntityName");
                    EntityType entityType = EntityType.fromName(entityName);
                    if (entityType != null) {
                        commandEntities.put(entityId, entityType);
                    } else {
                        getLogger().warning("Invalid entity type in config: " + entityName);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onDropEgg(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) {
            return;
        }

        Player player = event.getEntity().getKiller();
        EntityType entityType = event.getEntityType();
        
        // Check if entity is blacklisted
        if (blacklistedDropEgg.contains(entityType)) {
            return;
        }
        // Spawn spawner egg with given chance
         if (dropEggEnable && new Random().nextDouble() < eggDropChance) {
            ItemStack spawnEgg = new ItemStack(Material.MONSTER_EGG, 1, entityType.getTypeId());
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), spawnEgg);
        } 
    }
    
    @EventHandler
    public void onCurrencyDrops(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) {
            return;
        }

        Player player = event.getEntity().getKiller();
        EntityType entityType = event.getEntityType();
        
        if (blacklistedСurrencyDrop.contains(entityType)) {
            return;
        }

        // Drop currency with given chance
        if (new Random().nextDouble() < currencyDropChance) {
            int min = CurrencyDropsMin;
            int max = CurrencyDropsMax;
            int amount = new Random().nextInt(max - min + 1) + min;

            player.sendMessage(getCurrencyMessage(entityType, amount).replace("&", "§"));
        }
    }

    @EventHandler
    public void onCommandEntities(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) {
            return;
        }

        Player player = event.getEntity().getKiller();
        EntityType entityType = event.getEntityType();
        

        // Execute command for specific entities
        for (Map.Entry<Integer, EntityType> entry : commandEntities.entrySet()) {
            if (entry.getValue() == entityType) {
                String command = getConfig().getString("Entity." + entry.getKey() + ".Command");
                if (command != null && !command.isEmpty()) {
                    command = command.replace("{player}", player.getName());
                    command = command.replace("{entity}", entityType.name());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mobloot")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("mobloot.reload")) {
                    reloadConfig();
                    loadConfigValues();
                    sender.sendMessage(configReloadMessage.replace("&", "§"));
                    return true;
                } else {
                    sender.sendMessage(noPermissionMessage.replace("&", "§"));
                    return true;
                }
            }
        }
        return false;
    }
}
