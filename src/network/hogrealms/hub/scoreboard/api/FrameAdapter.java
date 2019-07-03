package network.hogrealms.hub.scoreboard.api;

import org.bukkit.entity.Player;

import java.util.List;

public interface FrameAdapter {
  String getTitle(Player var1);

  List<String> getLines(Player var1);
}
