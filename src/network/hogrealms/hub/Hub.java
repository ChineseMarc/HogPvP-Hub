package network.hogrealms.hub;

import network.hogrealms.hub.handlers.ServersHandler;
import network.hogrealms.hub.listeners.Listener;
import network.hogrealms.hub.queue.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Hub extends JavaPlugin {
  private Hub plugin;
  private ServersHandler serversHandler;
  private QueueManager queueManager;

  @Override
  public void onEnable() {
    plugin = this;
    getConfig().options().copyDefaults(true);
    saveConfig();
    worldSettings();
    PluginManager pluginManager = Bukkit.getServer().getPluginManager();
    serversHandler = new ServersHandler(this);
    queueManager = new QueueManager(this, serversHandler);
    pluginManager.registerEvents(new Listener(this, serversHandler, queueManager), this);
  }

  @Override
  public void onDisable() {
    plugin = null;
    serversHandler = null;
    queueManager = null;
	  
  }

  private void worldSettings() {
    World world = getServer().getWorlds().get(0);
    world.setAmbientSpawnLimit(0);
    world.setAnimalSpawnLimit(0);
    world.setDifficulty(org.bukkit.Difficulty.PEACEFUL);
    world.setWeatherDuration(0);
    world.setKeepSpawnInMemory(false);
    world.setMonsterSpawnLimit(0);
    world.setPVP(false);
    world.setStorm(false);
    world.setTime(0L);
  }

  /** @return Hub instance */
  public Hub getPlugin() {
    return plugin;
  }

  /** @return ServersHandler instance */
  public ServersHandler getServersHandler() {
    return serversHandler;
  }

  /** @return QueueManager instance */
  public QueueManager getQueueManager() {
    return queueManager;
  }
}
