package com.xexmc.ride;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Core
  extends JavaPlugin
  implements Listener
{
  protected FileConfiguration config;
  
  public void onEnable()
  {
    this.config = getConfig();
    if (!this.config.contains("message"))
    {
      this.config.set("message", "§9Stacker> §7You where stacked by §e<player>.");
      saveConfig();
    }
    if (!this.config.contains("ejectmessage"))
    {
      this.config.set("ejectmessage", "§9Stacker> §7You where ejected by §e<player>.");
      saveConfig();
    }
    getServer().getPluginManager().registerEvents(this, this);
  }
  
  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
  {
    if ((event.getRightClicked() instanceof Player))
    {
      Player player = event.getPlayer();
      if ((!duckEjectPassenger(player, event.getRightClicked())) && (playerCanRide(player)))
      {
        Player vehicle = getVehicle(player);
        if (vehicle == null)
        {
          vehicle = (Player)event.getRightClicked();
          Player duck = getRootVehicle(vehicle);
          if (duck.hasPermission("rank.normal"))
          {
            getLastPassenger(vehicle).setPassenger(player);
            alertPlayers(player, duck, "message");
          }
        }
        else
        {
          vehicle.eject();
        }
      }
    }
  }
  
  private boolean playerCanRide(Player player)
  {
    return (player.hasPermission("rank.owner")) && (player.getPassenger() == null);
  }
  
  private boolean duckEjectPassenger(Player duck, Entity passenger)
  {
    if ((duck.hasPermission("rank.normal")) && 
      (passenger.equals(duck.getPassenger())))
    {
      duck.eject();
      alertPlayers((Player)passenger, duck, "ejectmessage");
      
      return true;
    }
    return false;
  }
  
  private Player getRootVehicle(Player vehicle)
  {
    while (getVehicle(vehicle) != null) {
      vehicle = getVehicle(vehicle);
    }
    return vehicle;
  }
  
  private Player getLastPassenger(Player vehicle)
  {
    while ((vehicle.getPassenger() != null) && ((vehicle.getPassenger() instanceof Player))) {
      vehicle = (Player)vehicle.getPassenger();
    }
    return vehicle;
  }
  
  private Player getVehicle(Player player)
  {
    for (Player onlinePlayer : getServer().getOnlinePlayers())
    {
      Entity passenger = onlinePlayer.getPassenger();
      if (((passenger instanceof Player)) && (passenger.getEntityId() == player.getEntityId())) {
        return onlinePlayer;
      }
    }
    return null;
  }
  
  private void alertPlayers(Player player, Player duck, String key)
  {
    String message = this.config.getString(key);
    if (!message.isEmpty()) {
      getServer().broadcastMessage(message.replace("<player>", player.getName()).replace("<duck>", duck.getName()));
    }
  }
}
