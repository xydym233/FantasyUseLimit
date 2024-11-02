package cn.xydym.fantasyuse;

import java.util.Iterator;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NotExplode implements Listener {
    private final boolean notExplodeEnabled;

    public NotExplode(JavaPlugin javaPlugin) {
        this.notExplodeEnabled = javaPlugin.getConfig().getBoolean("NotExplodeKG", true);
    }

    @EventHandler(
            ignoreCancelled = true
    )
    public void onEntityExplode(EntityExplodeEvent explodeEvent) {
        if (this.notExplodeEnabled) {
            List<Block> blocks = explodeEvent.blockList();
            Iterator<Block> iterator = blocks.iterator();

            while(iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }

    }
}
