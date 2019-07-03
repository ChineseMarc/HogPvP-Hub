package network.hogrealms.hub.scoreboard.api;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FrameListener implements Listener {
  private Frame frame;

  FrameListener(Frame frame) {
    this.frame = frame;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    frame
        .getBoards()
        .put(event.getPlayer().getUniqueId(), new FrameBoard(event.getPlayer(), frame));
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    frame.getBoards().remove(event.getPlayer().getUniqueId());
  }
}
