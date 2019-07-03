package network.hogrealms.hub.scoreboard.api;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

class FrameBoardEntry {
  private final FrameBoard board;
  private String text;
  private String identifier;
  private Team team;

  FrameBoardEntry(FrameBoard board, String text) {
    this.board = board;
    this.text = text;
    this.identifier = this.board.getUniqueIdentifier(text);
    this.setup();
  }

  void setup() {
    Scoreboard scoreboard = this.board.getScoreboard();
    String teamName = this.identifier;
    if (teamName.length() > 16) {
      teamName = teamName.substring(0, 16);
    }

    Team team = scoreboard.getTeam(teamName);
    if (team == null) {
      team = scoreboard.registerNewTeam(teamName);
    }

    if (!team.getEntries().contains(this.identifier)) {
      team.addEntry(this.identifier);
    }

    if (!this.board.getEntries().contains(this)) {
      this.board.getEntries().add(this);
    }

    this.team = team;
  }

  void send(int position) {
    if (this.text.length() > 16) {
      String prefix = this.text.substring(0, 16);
      String suffix;
      if (prefix.charAt(15) == 167) {
        prefix = prefix.substring(0, 15);
        suffix = this.text.substring(15);
      } else {
        if (prefix.charAt(14) == 167) {
          prefix = prefix.substring(0, 14);
          suffix = this.text.substring(14);
        } else {
          if (ChatColor.getLastColors(prefix)
              .equalsIgnoreCase(ChatColor.getLastColors(this.identifier))) {
            suffix = this.text.substring(16);
          } else {
            suffix = ChatColor.getLastColors(prefix) + this.text.substring(16);
          }
        }
      }

      if (suffix.length() > 16) {
        suffix = suffix.substring(0, 16);
      }

      this.team.setPrefix(prefix);
      this.team.setSuffix(suffix);
    } else {
      this.team.setPrefix(this.text);
      this.team.setSuffix("");
    }

    Score score = this.board.getObjective().getScore(this.identifier);
    score.setScore(position);
  }

  void remove() {
    this.board.getIdentifiers().remove(this.identifier);
    this.board.getScoreboard().resetScores(this.identifier);
  }

  void setText(String line) {
    this.text = line;
  }
}
