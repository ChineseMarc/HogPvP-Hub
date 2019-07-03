package network.hogrealms.hub.queue;

import network.hogrealms.hub.Hub;
import network.hogrealms.hub.handlers.ServersHandler;
import network.hogrealms.hub.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class QueueManager implements Listener {
  private ArrayList<Queue> queues = new ArrayList<>();

  public QueueManager(Hub i, ServersHandler serversHandler) {
    serversHandler
        .getNetworkServers()
        .forEach((serverName, networkServer) -> queues.add(new Queue(serverName, i, this)));
    Bukkit.getPluginManager().registerEvents(this, i);
    for (Queue q : queues) {
      Bukkit.getConsoleSender()
          .sendMessage(Utils.colors("§7[§3Hub§7] &e" + q.getServer() + " is added for queue."));
    }
    Bukkit.getScheduler()
        .runTaskTimer(
            (Plugin) this,
            () -> {
              for (Queue q : queues) {
                if ((!q.isPaused()) && (!q.getPlayers().isEmpty())) {
                  q.sendFirst();
                  q.removeEntry(q.getPlayerAt(0));
                }
              }
            },
            20l,
            20l);
  }

  public void clear() {
    queues.clear();
  }

  private Queue getQueue(Player p) {
    for (Queue q : queues) {
      if (q.getPlayers().contains(p)) {
        return q;
      }
    }
    return null;
  }

  public Queue getQueue(String s) {
    for (Queue q : queues) {
      if (q.getServer().equalsIgnoreCase(s)) {
        return q;
      }
    }
    return null;
  }

  public String getQueueName(Player p) {
    return getQueue(p).getServer();
  }

  public int getPriority(Player player) {
    if (player.hasPermission("queue.bypass")) {
      return 0;
    }
    if (player.hasPermission("queue.1")) {
      return 1;
    }
    if (player.hasPermission("queue.2")) {
      return 2;
    }
    if (player.hasPermission("queue.3")) {
      return 3;
    }
    if (player.hasPermission("queue.4")) {
      return 4;
    }
    if (player.hasPermission("queue.5")) {
      return 5;
    }
    return 6;
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    for (Queue q : queues) {
      if (q.getPlayers().contains(p)) {
        q.removeEntry(p);
      }
      if (!q.getTaskMap().containsKey(p)) {
        continue;
      }
      (q.getTaskMap().get(p)).cancel();
      q.getTaskMap().remove(p);
    }
  }

  public List<Queue> getQueues() {
    return queues;
  }
}
