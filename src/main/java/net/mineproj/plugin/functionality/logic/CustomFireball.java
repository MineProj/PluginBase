package net.mineproj.plugin.functionality.logic;

import net.mineproj.plugin.functionality.ballistics.ShotTemplates;
import net.mineproj.plugin.functionality.client.PhantomWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CustomFireball implements Listener {
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Fireball) {
            event.setCancelled(true);
            ShotTemplates.fireShot(event.getLocation());
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFireChargeUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (!event.getAction().isRightClick()
                        || item == null
                        || item.getType() != Material.FIRE_CHARGE) {
            return;
        }
        event.setCancelled(true);
        item.setAmount(item.getAmount() - 1);
        if (item.getAmount() <= 0) {
            player.getInventory().setItemInMainHand(null);
        }
        ShotTemplates.launchFireball(player);
    }
}
