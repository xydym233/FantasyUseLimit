package cn.xydym.fantasyuse;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class BlockPhysicsListener implements Listener {

    private final List<String> noCactusWorlds;

    public BlockPhysicsListener(LoadFantasy plugin) {
        // 读取配置文件
        FileConfiguration config = plugin.getConfig();
        noCactusWorlds = config.getStringList("NoCactusWorld");
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        World world = event.getBlock().getWorld();
        if (noCactusWorlds.contains(world.getName())) {
            if (event.getBlock().getType() == Material.CACTUS) {
                event.getBlock().setType(Material.AIR); // 删除仙人掌
            }
        }
    }
}