package network.hogrealms.hub.scoreboard.api;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FrameBoard {
  private final ArrayList<FrameBoardEntry> entries = new ArrayList<>();
  private final ArrayList<String> identifiers = new ArrayList<>();
  private Scoreboard scoreboard;
  private Objective objective;
  private Frame frame;

  public FrameBoard(Player player, Frame frame) {
    this.setup(player);
    this.frame = frame;
  }

  private static String getRandomChatColor() {
    return ChatColor.values()[ThreadLocalRandom.current().nextInt(ChatColor.values().length)]
        .toString();
  }

  private void setup(Player player) {
    if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
      this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    } else {
      this.scoreboard = player.getScoreboard();
    }

    this.objective = this.scoreboard.registerNewObjective("Default", "dummy");
    this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    this.objective.setDisplayName(frame.getAdapter().getTitle(player));
    player.setScoreboard(this.scoreboard);
  }

  public FrameBoardEntry getEntryAtPosition(int pos) {
    return pos >= this.entries.size() ? null : this.entries.get(pos);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  public String getUniqueIdentifier(String text) {
    String identifier;
    for (identifier = getRandomChatColor() + ChatColor.WHITE;
        this.identifiers.contains(identifier);
        identifier = identifier + getRandomChatColor() + ChatColor.WHITE) {}
    if (identifier.length() > 16) {
      return this.getUniqueIdentifier(text);
    } else {
      this.identifiers.add(identifier);
      return identifier;
    }
  }

  public Scoreboard getScoreboard() {
    return this.scoreboard;
  }

  public List<FrameBoardEntry> getEntries() {
    return this.entries;
  }

  public Objective getObjective() {
    return this.objective;
  }

  public List<String> getIdentifiers() {
    return this.identifiers;
  }
}
