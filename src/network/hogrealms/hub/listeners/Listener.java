package network.hogrealms.hub.listeners;

import network.hogrealms.hub.Hub;
import network.hogrealms.hub.handlers.ServersHandler;
import network.hogrealms.hub.queue.QueueManager;
import network.hogrealms.hub.utils.ItemBuilder;
import network.hogrealms.hub.utils.NetworkServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Listener implements org.bukkit.event.Listener {
  private Hub plugin;
  private ServersHandler serversHandler;
  private ItemStack serverSelector;
  private int serverSelectorSlot;
  private List<String> joinMessage;
  private String serverSelectorName;
  private String world;
  private String guiName;
  private Double x;
  private Double y;
  private Double z;
  private int yaw;
  private int pitch;
  private int guiSize;
  private HashMap<String, String> networkServersLores = new HashMap<>();
  private QueueManager queueManager;

  public Listener(Hub instance, ServersHandler serversHandler, QueueManager queueManager) {
    this.queueManager = queueManager;
    this.serversHandler = serversHandler;
    plugin = instance;
    serverSelectorName =
        plugin.getConfig().getString("JoinItems.Selector.Name").replaceAll("&", "§");
    List<String> newList = new ArrayList<>();
    plugin
        .getConfig()
        .getStringList("JoinItems.Selector.Lore")
        .forEach((String s) -> newList.add(s.replaceAll("&", "§")));
    serverSelector =
        new ItemBuilder(Material.COMPASS)
            .setName(serverSelectorName)
            .setLore(newList)
            .toItemStack();
    serverSelectorSlot = plugin.getConfig().getInt("JoinItems.Selector.Slot");
    joinMessage = plugin.getConfig().getStringList("JoinMessage");
    world = plugin.getConfig().getString("Spawn.world");
    x = plugin.getConfig().getDouble("Spawn.x");
    y = plugin.getConfig().getDouble("Spawn.y");
    z = plugin.getConfig().getDouble("Spawn.z");
    yaw = plugin.getConfig().getInt("Spawn.yaw");
    pitch = plugin.getConfig().getInt("Spawn.pitch");
    guiSize = plugin.getConfig().getInt("GUI.size");
    guiName = plugin.getConfig().getString("GUI.name").replaceAll("&", "§");
    serversHandler
        .getNetworkServers()
        .forEach(
            (String s, NetworkServer networkServer) -> {
              if (!s.equalsIgnoreCase("all")) {
                networkServersLores.put(
                    networkServer.getGUIName().replaceAll("&", "§"), networkServer.getServerName());
              }
            });
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    final Player player = e.getPlayer();
    e.setJoinMessage(null);
    for (int i = 0; i < 10; i++) {
      player.sendMessage("");
    }
    for (String msg : joinMessage) {
      player.sendMessage(
          ChatColor.translateAlternateColorCodes('&', msg)
              .replaceAll("%player%", player.getName()));
    }
    player.getInventory().clear();
    Bukkit.getServer()
        .getScheduler()
        .scheduleSyncDelayedTask(
            plugin,
            () -> {
              player.getInventory().clear();
              player.getInventory().setItem(serverSelectorSlot, serverSelector);
            },
            2L);
    player.getInventory().setHeldItemSlot(4);
    player.setFoodLevel(20);
    if (player.getWalkSpeed() != 0.5F) {
      player.setWalkSpeed(0.5F);
    }
    player.teleport(new Location(Bukkit.getWorld(world), x, y, z));
    player.getLocation().setYaw(yaw);
    player.getLocation().setPitch(pitch);
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    ItemStack item = event.getItem();
    if ((item == null) || (item.getType().equals(Material.AIR))) {
      return;
    }
    if (!item.hasItemMeta()) {
      return;
    }
    if (!item.getItemMeta().hasDisplayName()) {
      return;
    }
    if ((item.getType() == Material.COMPASS)
        && (item.getItemMeta().getDisplayName().equals(serverSelectorName))) {
      player.openInventory(selector(player));
    }
  }

  @SuppressWarnings("deprecation")
  private Inventory selector(Player player) {
    Inventory inv = Bukkit.createInventory(null, guiSize, guiName);
    serversHandler
        .getNetworkServers()
        .forEach(
            (name, networkServer) -> {
              if (name.equalsIgnoreCase("all")) return;
              int slot = plugin.getConfig().getInt("Servers." + name + ".gui.slot");
              String id = plugin.getConfig().getString("Servers." + name + ".gui.item");
              String guiName = plugin.getConfig().getString("Servers." + name + ".gui.name");
              ArrayList<String> newList = new ArrayList<>();
              for (String s : plugin.getConfig().getStringList("Servers." + name + ".gui.lore")) {
                newList.add(
                    s.replaceAll("&", "§")
                        .replaceAll(
                            "%online_" + name + "%",
                            networkServer.isOnline() ? "online" : "offline")
                        .replaceAll("%count_" + name + "%", "" + networkServer.getPlayerCount()));
              }
              ItemStack itemStack =
                  new ItemBuilder(Material.getMaterial(id))
                      .setName(guiName.replaceAll("&", "§"))
                      .setLore(newList)
                      .setAmount(networkServer.getPlayerCount())
                      .toItemStack();
              inv.setItem(slot, itemStack);
            });
    return inv;
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    final Player player = (Player) event.getWhoClicked();
    final ItemStack item = event.getCurrentItem();
    final InventoryAction action = event.getAction();
    if (event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
      event.setCancelled(true);
      return;
    }
    if (((item == null) || (item.getType().equals(Material.AIR)))
        && ((action.equals(InventoryAction.HOTBAR_SWAP))
            || (action.equals(InventoryAction.SWAP_WITH_CURSOR)))) {
      event.setCancelled(true);
      return;
    }
    if ((item.hasItemMeta()) && (item.getItemMeta().hasDisplayName())) {
      if (item.getItemMeta().getDisplayName().equals(serverSelectorName.replaceAll("&", "§"))) {
        event.setCancelled(true);
        player.updateInventory();
      }
      if (event.getInventory().getName().equalsIgnoreCase(guiName)) {
        event.setCancelled(true);
        String displayName = item.getItemMeta().getDisplayName();
        if (networkServersLores.containsKey(displayName)) {
          String serverName = networkServersLores.get(displayName);
          player.closeInventory();
          queueManager.getQueue(serverName).addEntry(player);
        }
      }
    }
  }
}
