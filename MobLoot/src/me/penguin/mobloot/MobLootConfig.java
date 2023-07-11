package me.penguin.mobloot;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

public class MobLootConfig {
	private mobloot plugin;
    private double eggDropChance;
    private boolean dropEggEnable;
    private boolean renameEgg;
    private boolean spawnFromEgg;
    private String configReloadMessage;
    private String noPermissionMessage;
    private List<EntityType> blacklistedDropEgg;
    private Map<Integer, EntityType> commandEntities;
    private boolean dropItemEnable;
    private double currencyDropChance;
    private int currencyDropsMin;
    private int currencyDropsMax;
    private Material dropItemMaterial;
    private List<EntityType> blacklistedCurrencyDrop;
    private String currencyMessage;
    private String dropItemMessage;

    public MobLootConfig(ConfigurationSection configSection) {
        eggDropChance = configSection.getDouble("DropEgg.Chance", 0.1);
        dropEggEnable = configSection.getBoolean("DropEgg.Enable", true);
        renameEgg = configSection.getBoolean("DropEgg.Rename", true);
        spawnFromEgg = configSection.getBoolean("DropEgg.SpawnFromEgg", true);
        configReloadMessage = configSection.getString("ConfigReloadMsg", "§aConfig reloaded");
        noPermissionMessage = configSection.getString("NotPermMsg", "§cYou do not have permission.");

        blacklistedDropEgg = new ArrayList<>();
        List<String> dropEggBlacklist = configSection.getStringList("DropEgg.Blacklist");
        for (String entityName : dropEggBlacklist) {
            EntityType entityType = EntityType.fromName(entityName);
            if (entityType != null) {
                blacklistedDropEgg.add(entityType);
            } else {
                plugin.getLogger().warning("Invalid entity type in DropEgg.Blacklist: " + entityName);
            }
        }

        commandEntities = new HashMap<>();
        ConfigurationSection entitySection = configSection.getConfigurationSection("Entity");
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
                        plugin.getLogger().warning("Invalid entity type in Entity." + key + ".EntityName: " + entityName);
                    }
                }
            }
        }

        dropItemEnable = configSection.getBoolean("CurrencyDrop.DropItem.Enable", false);
        currencyDropChance = configSection.getDouble("CurrencyDrop.Chance", 0.1);
        currencyDropsMin = configSection.getInt("CurrencyDrop.MinAmount", 10);
        currencyDropsMax = configSection.getInt("CurrencyDrop.MaxAmount", 100);
        dropItemMaterial = Material.getMaterial(configSection.getString("CurrencyDrop.DropItem.Material", "GOLD_INGOT"));

        blacklistedCurrencyDrop = new ArrayList<>();
        List<String> currencyDropBlacklist = configSection.getStringList("CurrencyDrop.Blacklist");
        for (String entityName : currencyDropBlacklist) {
            EntityType entityType = EntityType.fromName(entityName);
            if (entityType != null) {
                blacklistedCurrencyDrop.add(entityType);
            } else {
                plugin.getLogger().warning("Invalid entity type in CurrencyDrop.Blacklist: " + entityName);
            }
        }

        currencyMessage = configSection.getString("CurrencyDrop.Message");
        dropItemMessage = configSection.getString("CurrencyDrop.DropItem.Message");
    }

    public double getEggDropChance() {
        return eggDropChance;
    }

    public boolean isDropEggEnabled() {
        return dropEggEnable;
    }

    public boolean isRenameEggEnabled() {
        return renameEgg;
    }

    public String getConfigReloadMessage() {
        return configReloadMessage;
    }

    public String getNoPermissionMessage() {
        return noPermissionMessage;
    }

    public List<EntityType> getBlacklistedDropEgg() {
        return blacklistedDropEgg;
    }

    public Map<Integer, EntityType> getCommandEntities() {
        return commandEntities;
    }

    public boolean isDropItemEnabled() {
        return dropItemEnable;
    }
    
    public boolean isSpawnFromEgg() {
        return spawnFromEgg;
    }

    public double getCurrencyDropChance() {
        return currencyDropChance;
    }

    public int getCurrencyDropsMin() {
        return currencyDropsMin;
    }

    public int getCurrencyDropsMax() {
        return currencyDropsMax;
    }

    public Material getDropItemMaterial() {
        return dropItemMaterial;
    }

    public List<EntityType> getBlacklistedCurrencyDrop() {
        return blacklistedCurrencyDrop;
    }

    public String getCurrencyMessage() {
        return currencyMessage;
    }

    public String getDropItemMessage() {
        return dropItemMessage;
    }
}