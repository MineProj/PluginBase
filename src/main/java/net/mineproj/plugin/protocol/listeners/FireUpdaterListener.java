package net.mineproj.plugin.protocol.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.mineproj.plugin.PluginBase;
import net.mineproj.plugin.functionality.client.PhantomWorld;
import net.mineproj.plugin.protocol.data.ProtocolPlugin;
import net.mineproj.plugin.utils.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FireUpdaterListener extends PacketAdapter {

    public FireUpdaterListener() {
        super(PluginBase.getInstance(),
                        PacketType.Play.Client.USE_ITEM,
                        PacketType.Play.Client.ARM_ANIMATION);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() != Material.FIRE_CHARGE) {
            return;
        }
        for (Block block : scanArea3x3(
                        ProtocolPlugin.getProtocol(
                        event.getPlayer()).getLocation())) {
            if (block.getType().isAir() || block.getType().equals(Material.FIRE)) {
                PhantomWorld.setBlock(event.getPlayer(),
                                block.getLocation(), Material.AIR);
            }
        }
    }

    public List<Block> scanArea3x3(Location center) {
        List<Block> blocks = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = BlockUtil.getBlockAsync(new Location(
                                    center.getWorld(),
                                    center.getBlockX() + x,
                                    center.getBlockY() + y,
                                    center.getBlockZ() + z
                    ));
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }
}
