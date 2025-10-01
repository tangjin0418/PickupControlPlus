package org.tjdev.custom.pickupcontrolplus.handler;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.tjdev.custom.pickupcontrolplus.config.Config;
import org.tjdev.util.tjpluginutil.spigot.ItemUtil;
import org.tjdev.util.tjpluginutil.spigot.object.IncludedListener;

import static org.tjdev.custom.pickupcontrolplus.handler.DataHandler.DATA_PLAYER;

public class PickupHandler implements IncludedListener {
    @EventHandler
    public void pickup(EntityPickupItemEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) return;
        var dp = DATA_PLAYER.get((Player) e.getEntity());
        if (dp == null) {
            e.setCancelled(true);
            return;
        }
        if (dp.disabled.contains(e.getItem().getItemStack().getType())) e.setCancelled(true);
    }

    @EventHandler
    public void force(PlayerInteractEvent e) {
        if (!Config.CONFIG.forcePickupMode) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (!e.getAction().isRightClick()) return;
        if (e.getInteractionPoint() == null) return;
        var dp = DATA_PLAYER.get(e.getPlayer());
        if (dp == null) return;
        for (Entity nearbyEntity : e.getInteractionPoint().getNearbyEntities(0.3, 0.3, 0.3)) {
            if (!(nearbyEntity instanceof Item i)) continue;
            if (dp.disabled.contains(i.getItemStack().getType())) {
                ItemUtil.give(e.getPlayer(), i.getItemStack().clone());
                e.getPlayer().playPickupItemAnimation(i);
                i.setItemStack(new ItemStack(Material.AIR));
            }
        }
    }
}
