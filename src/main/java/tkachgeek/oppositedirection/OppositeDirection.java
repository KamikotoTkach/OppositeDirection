package tkachgeek.oppositedirection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class OppositeDirection extends JavaPlugin implements Listener {
  public static final String DISABLED_FOR_PATH = "disabledFor";
  List<Material> affected = new ArrayList<>();
  List<UUID> playersDisabledFeature = new ArrayList<>();
  
  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(this, this);
    
    getConfig().addDefault("affected", Stream.of(Material.PISTON, Material.STICKY_PISTON, Material.DISPENSER, Material.DROPPER, Material.OBSERVER, Material.HOPPER, Material.FURNACE, Material.CHEST, Material.IRON_TRAPDOOR).map(Enum::name).collect(Collectors.toList()));
    getConfig().addDefault("message.disabled","This feature is disabled for you");
    getConfig().addDefault("message.enabled", "This feature is enabled for you");
    getConfig().addDefault(DISABLED_FOR_PATH, new ArrayList<>());
    getConfig().options().copyDefaults(true);
    saveConfig();
    
    affected = getConfig().getStringList("affected").stream().map(Material::valueOf).collect(Collectors.toList());
    playersDisabledFeature = getConfig().getStringList(DISABLED_FOR_PATH).stream().map(UUID::fromString).collect(Collectors.toList());
    
    getCommand("oppositeDirection").setExecutor(this);
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      if (playersDisabledFeature.remove(((Player) sender).getUniqueId())) {
        sender.sendMessage(getConfig().getString("message.enabled"));
      } else {
        playersDisabledFeature.add(((Player) sender).getUniqueId());
        sender.sendMessage(getConfig().getString("message.disabled"));
  
      }
    } else {
      sender.sendMessage("This command is only available for players");
    }
    return true;
  }
  
  @Override
  public void onDisable() {
    getConfig().set(DISABLED_FOR_PATH, playersDisabledFeature.stream().map(UUID::toString).collect(Collectors.toList()));
    saveConfig();
  }
  
  @EventHandler
  public void blockPlace(BlockPlaceEvent event) {
    
    if (event.getPlayer().isSneaking()
       && !playersDisabledFeature.contains(event.getPlayer().getUniqueId())
       && event.getHand() == EquipmentSlot.OFF_HAND
       && affected.contains(event.getBlock().getType())
       && event.getBlock().getBlockData() instanceof Directional) {
      
      Directional directional = (Directional) event.getBlock().getBlockData();
      directional.setFacing(directional.getFacing().getOppositeFace());
      event.getBlock().setBlockData(directional);
    }
  }
}
