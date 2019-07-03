package network.hogrealms.hub.scoreboard.api;

import network.hogrealms.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Frame {
  private Frame instance;
  private Hub plugin;
  private FrameAdapter adapter;
  private Map<UUID, FrameBoard> boards;

  public Frame(Hub plugin, final FrameAdapter adapter) {
    if (instance != null) {
      throw new RuntimeException("Frame has already been instantiated!");
    }
    instance = this;
    this.plugin = plugin;
    this.adapter = adapter;
    this.boards = new HashMap<>();
    this.setup();
  }

  private void setup() {
    plugin.getServer().getPluginManager().registerEvents(new FrameListener(this), this.plugin);
    AtomicReference<FrameBoard> board = new AtomicReference<>();
    AtomicReference<Scoreboard> scoreboard = new AtomicReference<>();
    AtomicReference<Objective> objective = new AtomicReference<>();
    AtomicReference<List<String>> newLines = new AtomicReference<>();
    AtomicInteger i = new AtomicInteger();
    AtomicReference<FrameBoardEntry> entry = new AtomicReference<>();
    AtomicInteger j = new AtomicInteger();
    AtomicReference<FrameBoardEntry> entry2 = new AtomicReference<>();
    AtomicReference<String> line = new AtomicReference<>();
    this.plugin
        .getServer()
        .getScheduler()
        .runTaskTimerAsynchronously(
            this.plugin,
            () -> {
              for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                board.set(this.boards.get(player.getUniqueId()));
                if (board.get() != null) {
                  scoreboard.set(board.get().getScoreboard());
                  objective.set(board.get().getObjective());
                  if (!objective.get().getDisplayName().equals(this.adapter.getTitle(player))) {
                    objective.get().setDisplayName(this.adapter.getTitle(player));
                  }
                  newLines.set(this.adapter.getLines(player));
                  if (newLines.get() != null && !newLines.get().isEmpty()) {
                    Collections.reverse(newLines.get());
                    if (board.get().getEntries().size() > newLines.get().size()) {
                      for (i.set(newLines.get().size());
                          i.get() < board.get().getEntries().size();
                          i.incrementAndGet()) {
                        entry.set(board.get().getEntryAtPosition(i.get()));
                        if (entry.get() != null) {
                          entry.get().remove();
                        }
                      }
                    }
                    for (j.set(0); j.get() < newLines.get().size(); j.incrementAndGet()) {
                      entry2.set(board.get().getEntryAtPosition(j.get()));
                      line.set(
                          ChatColor.translateAlternateColorCodes('&', newLines.get().get(j.get())));
                      if (entry2.get() == null) {
                        entry2.set(new FrameBoardEntry(board.get(), line.get()));
                      }
                      entry2.get().setText(line.get());
                      entry2.get().setup();
                      entry2.get().send(j.get());
                    }
                  } else {
                    board.get().getEntries().forEach(FrameBoardEntry::remove);
                    board.get().getEntries().clear();
                  }
                  player.setScoreboard(scoreboard.get());
                }
              }
            },
            20L,
            2L);
  }

  public Frame getInstance() {
    return instance;
  }

  public FrameAdapter getAdapter() {
    return this.adapter;
  }

  public Map<UUID, FrameBoard> getBoards() {
    return this.boards;
  }
}
