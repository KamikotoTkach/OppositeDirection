package tkachgeek.oppositedirection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class OppositeDirection extends JavaPlugin implements Listener {
  List<Material> affected = new ArrayList<>();
  
  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(this, this);
    getConfig().addDefault("affected", Stream.of(Material.PISTON, Material.STICKY_PISTON, Material.DISPENSER, Material.DROPPER, Material.OBSERVER, Material.HOPPER, Material.FURNACE, Material.CHEST, Material.IRON_TRAPDOOR).map(Enum::name).collect(Collectors.toList()));
    getConfig().options().copyDefaults(true);
    saveConfig();
    affected = getConfig().getStringList("affected").stream().map(Material::valueOf).collect(Collectors.toList());
  }
  
  @Override
  public void onDisable() {
  }
  
  @EventHandler
  public void blockPlace(BlockPlaceEvent event) {
    
    if (event.getPlayer().isSneaking()
       && event.getHand() == EquipmentSlot.OFF_HAND
       && affected.contains(event.getBlock().getType())
       && event.getBlock().getBlockData() instanceof Directional) {
      
      Directional directional = (Directional) event.getBlock().getBlockData();
      directional.setFacing(directional.getFacing().getOppositeFace());
      event.getBlock().setBlockData(directional);
      event.getBlock().getState().update();
    }
  }
}
