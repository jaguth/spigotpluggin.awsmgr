package com.jaguth.spigotpluggin.awsmgr.listeners;

import com.jaguth.spigotpluggin.awsmgr.AwsMgr;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {
    private AwsMgr awsMgr;

    public BlockListener(AwsMgr awsMgr) {
        this.awsMgr = awsMgr;
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent blockBreakEvent) {
        Block block = blockBreakEvent.getBlock();

        if (block.getType() == Material.OAK_SIGN) {
            Sign sign = (Sign) block;
            Bukkit.broadcastMessage("oak sign broke!");

            printSpawnedSignBlocks();

            for (Sign spawnedSign : awsMgr.getSpawnedSigns()) {
                Bukkit.broadcastMessage("in a sign");
               if (spawnedSign.getLines().equals(sign))  {
                   Bukkit.broadcastMessage("i equal sign!");
               }
            }
//
//            if (awsMgr.getSpawnedSigns().contains(block)) {
//                Bukkit.broadcastMessage("in spawned sign list!");
//            }

        }
    }

    public void printSpawnedSignBlocks() {
        for (Sign sign : awsMgr.getSpawnedSigns()) {
            String[] lines = sign.getLines();

            Bukkit.broadcastMessage("-- block of lines start --");

            for (int i = 0; i < lines.length; i++) {
                Bukkit.broadcastMessage(lines[i]);
            }
        }
    }

}
