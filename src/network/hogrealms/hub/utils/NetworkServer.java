package network.hogrealms.hub.utils;

public class NetworkServer {
  private String serverName;
  private String serverIP = "127.0.0.1";
  private int playerCount = 0;
  private int serverPort = 25565;
  private boolean isOnline = false;
  private String guiName = "";

  public NetworkServer(String serverName) {
    this.serverName = serverName;
  }

  public boolean isOnline() {
    return isOnline;
  }

  public void setOnline(boolean online) {
    isOnline = online;
  }

  public int getPlayerCount() {
    return playerCount;
  }

  public void setPlayerCount(int playerCount) {
    this.playerCount = playerCount;
  }

  public int getServerPort() {
    return serverPort;
  }

  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  public String getServerName() {
    return serverName;
  }

  public String getServerIP() {
    return serverIP;
  }

  public void setServerIP(String serverIP) {
    this.serverIP = serverIP;
  }

  public String getGUIName() {
    return guiName;
  }

  public void setGUIName(String name) {
    this.guiName = name;
  }
}
