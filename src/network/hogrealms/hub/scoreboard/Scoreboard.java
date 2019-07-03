package network.hogrealms.hub.scoreboard;

import fr.xephi.authme.api.v3.AuthMeApi;
import network.hogrealms.hub.Hub;
import network.hogrealms.hub.handlers.ServersHandler;
import network.hogrealms.hub.scoreboard.api.FrameAdapter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Deprecated
//TODO: Replace with new Scoreboard Class Soon
public class Scoreboard implements FrameAdapter {
  private Hub plugin;
  private String scoreboardTitle;
  private ServersHandler serversHandler;
  private AuthMeApi authMeApi;
  private List<String> notRegisteredPlayerScoreboard;
  private List<String> loginPlayerScoreboard;
  private List<String> PlayerScoreboard;

  public Scoreboard(Hub plugin, ServersHandler serversHandler) {
    this.plugin = plugin;
    this.serversHandler = serversHandler;
    scoreboardTitle = plugin.getConfig().getString("Scoreboard.title").replaceAll("&", "§");
    authMeApi = AuthMeApi.getInstance();
    notRegisteredPlayerScoreboard = plugin.getConfig().getStringList("Scoreboard.NotRegisteredLines");
    loginPlayerScoreboard = plugin.getConfig().getStringList("Scoreboard.LoginLines");
    PlayerScoreboard = plugin.getConfig().getStringList("Scoreboard.Lines");
  }

  @Override
  public String getTitle(Player player) {
    return scoreboardTitle;
  }

  @Override
  public List<String> getLines(Player player) {
    List<String> returnList = new ArrayList<>();
    if (authMeApi.isRegistered(player.getName())) {
      if (authMeApi.isAuthenticated(player)) {
        PlayerScoreboard.forEach(
            s -> returnList.add(
                s.replaceAll("%online_all%", "" + serversHandler.networkServerPlayerCount("ALL"))
                    .replaceAll("%player_name%", player.getName())
                    .replaceAll("%rank%", getFormattedRank(player))));
        return returnList;
      } else {
        loginPlayerScoreboard.forEach(
            s -> returnList.add(
                s.replaceAll("%online_all%", "" + serversHandler.networkServerPlayerCount("ALL"))
                    .replaceAll("%player_name%", player.getName())
                    .replaceAll("%rank%", getFormattedRank(player))));
        return returnList;
      }
    } else {
      notRegisteredPlayerScoreboard.forEach(
          s -> returnList.add(
              s.replaceAll("%online_all%", "" + serversHandler.networkServerPlayerCount("ALL"))
                  .replaceAll("%player_name%", player.getName())
                  .replaceAll("%rank%", getFormattedRank(player))));
      return returnList;
    }
  }

  // TODO: this
  private String getFormattedRank(Player player) {
    return "RANK";
  }
}
