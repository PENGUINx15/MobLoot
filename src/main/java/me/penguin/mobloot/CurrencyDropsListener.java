package me.penguin.mobloot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.penguin.mobloot.API.ConfigManager;
import me.penguin.mobloot.API.MessageManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.milkbowl.vault.economy.Economy;

public class CurrencyDropsListener implements Listener {

    private final MobLoot plugin;
    private final ConfigManager config;
    private final MessageManager messageManager;

    public CurrencyDropsListener(MobLoot plugin, ConfigManager config, MessageManager messageManager) {
        this.plugin = plugin;
        this.config = config;
        this.messageManager = messageManager;

    }


    private String getEntityName(EntityType entityType) {
        return WordUtils.capitalizeFully(entityType.name().replace("_", " "));
    }

    @EventHandler
    public void onCurrencyDrops(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        Player player = event.getEntity().getKiller();
        EntityType entityType = event.getEntityType();



        if (!config.getBoolen("congig.yml", "CurrencyDrop.DropItem.Enable")) {
            ItemStack dropItem = new ItemStack(Material.getMaterial(config.getString("congig.yml", "CurrencyDrop.DropItem.Material")));
            ItemMeta meta = dropItem.getItemMeta();
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
            dropItem.setItemMeta(meta);

            event.getDrops().add(dropItem);
        } else if (new Random().nextDouble() < config.getDouble("congig.yml", "CurrencyDrop.Chance")) {
            int min = config.getInt("congig.yml", "CurrencyDrop.MinAmount");
            int max = config.getInt("congig.yml", "CurrencyDrop.MaxAmount");
            int amount = new Random().nextInt(max - min + 1) + min;

            Economy economy = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
            economy.depositPlayer(player, amount);

            String message = config.getString("config.yml", "CurrencyDrop.Message");
            message = message.replace("{entity}", getEntityName(entityType));
            message = message.replace("{amount}", String.valueOf(amount));
            messageManager.sendMessage(player, message);
        }
    }

    private ArrayList<Object> getBlacklistedCurrencyDrop() {
        ArrayList<Object> blacklistedCurrencyDrop = new ArrayList<>();
        List<String> currencyDropBlacklist = config.getStringList("config.yml", "CurrencyDrop.Blacklist");
        for (String entityName : currencyDropBlacklist) {
            EntityType entityType = EntityType.fromName(entityName);
            if (entityType != null) {
                blacklistedCurrencyDrop.add(entityType);
            } else {
                messageManager.sendLog("warn", "Invalid entity type in CurrencyDrop.Blacklist: " + entityName);
            }
        }
        return blacklistedCurrencyDrop;
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!config.getBoolen("congig.yml", "CurrencyDrop.DropItem.Enable")) {
            return;
        }

        Player player =  event.getPlayer();
        ItemStack item = event.getItem().getItemStack();

        if (item.getType() == Material.getMaterial(config.getString("congig.yml", "CurrencyDrop.DropItem.Material")) ) {
            int min = config.getInt("congig.yml", "CurrencyDrop.MinAmount");
            int max = config.getInt("congig.yml", "CurrencyDrop.MaxAmount");
            int amount = new Random().nextInt(max - min + 1) + min;

            int dropItemCount = item.getAmount();
            for (int i = 0; i < dropItemCount; i++) {
                Economy economy = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
                economy.depositPlayer(player, amount);
            }

            String message = config.getString("config.yml", "CurrencyDrop.DropItem.Message");
            message = message.replace("{entity}", getEntityName(EntityType.UNKNOWN));
            message = message.replace("{amount}", String.valueOf(dropItemCount * amount));
            messageManager.sendMessage(player, message);


            event.getItem().remove();
            event.setCancelled(true);
        }
    }
}