package com.jaguth.spigotpluggin.awsmgr.listeners;

import com.jaguth.spigotpluggin.awsmgr.AwsMgr;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private AwsMgr awsMgr;

    public PlayerListener(AwsMgr awsMgr) {
        this.awsMgr = awsMgr;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        awsMgr.addPlayer(player);

        try {
            player.sendMessage(awsMgr.generatePrintInfo());
        }
        catch (Exception e) {
            player.sendMessage("Failed to generate print info: " + e.toString());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerBedLeaveEvent playerLeaveEvent) {
        Player player = playerLeaveEvent.getPlayer();
        awsMgr.removeAwsPlayer(player);
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent entityDeathEvent) {
        awsMgr.handleEntityDeath(entityDeathEvent.getEntity());
    }
}
