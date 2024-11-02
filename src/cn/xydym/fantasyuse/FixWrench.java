package cn.xydym.fantasyuse;

import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class FixWrench implements Listener {
    private final JavaPlugin javaPlugin;

    public FixWrench(JavaPlugin plugin) {
        this.javaPlugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (this.javaPlugin.getConfig().getBoolean("FixWrenchKG", true)) {
            Player player = event.getPlayer();
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Material blockType = event.getClickedBlock().getType();
                List<String> fnubList = this.javaPlugin.getConfig().getStringList("FixWrenchBlock");
                Iterator var5 = fnubList.iterator();

                while(true) {
                    Material fnubMaterial;
                    do {
                        if (!var5.hasNext()) {
                            return;
                        }

                        String fnubString = (String)var5.next();
                        fnubMaterial = Material.matchMaterial(fnubString);
                    } while(blockType != fnubMaterial);

                    ItemStack item = player.getItemInHand();
                    List<String> nobanshouList = this.javaPlugin.getConfig().getStringList("FixWrenchID");
                    Iterator var10 = nobanshouList.iterator();

                    while(var10.hasNext()) {
                        String nobanshouString = (String)var10.next();
                        Material nobanshouMaterial = Material.matchMaterial(nobanshouString);
                        if (item.getType() == nobanshouMaterial) {
                            player.sendMessage(this.javaPlugin.getConfig().getString("FixWrenchMessage"));
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }
}
