package net.mineproj.plugin.functionality.logic;

import net.mineproj.plugin.functionality.ballistics.ShotTemplates;
import net.mineproj.plugin.millennium.vectors.Vec2;
import net.mineproj.plugin.protocol.data.ProtocolPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CustomFireball implements Listener {
    public static Map<UUID, Vec2> directional = new ConcurrentHashMap<>();
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Fireball) {
            if (!directional.containsKey(event.getEntity().getUniqueId()))
                return;

            event.setCancelled(true);
            ShotTemplates.fireShot(event.getLocation(),
                            directional.get((event.getEntity()).getUniqueId()));
            directional.remove(event.getEntity().getUniqueId());
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onShadowChargeUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (!event.getAction().isRightClick()
                        || item == null
                        || item.getType() != Material.ENDER_EYE) {
            return;
        }
        event.setCancelled(true);
        item.setAmount(item.getAmount() - 1);
        if (item.getAmount() <= 0) {
            player.getInventory().setItemInMainHand(null);
        }
        ShotTemplates.sonicCannonShot(ProtocolPlugin.getProtocol(player));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGrenadeUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (!event.getAction().isRightClick()
                        || item == null
                        || item.getType() != Material.HEAVY_CORE) {
            return;
        }
        event.setCancelled(true);
        item.setAmount(item.getAmount() - 1);
        if (item.getAmount() <= 0) {
            player.getInventory().setItemInMainHand(null);
        }
        ShotTemplates.grenadeShot(ProtocolPlugin.getProtocol(player));
    }
}
