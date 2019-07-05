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
            //Bukkit.broadcastMessage("oak sign broke!");
            handleOakSignBreak((Sign) block.getState());
        }
    }

    private void handleOakSignBreak(Sign brokenSign) {
        //Bukkit.broadcastMessage("broken block sign:");
        //printSign(sign.getLines());

        awsMgr.destroyInstanceGroupsThatBelongingToDestroyedSign(brokenSign);

        //for (Sign spawnedSign : awsMgr.getSpawnedSigns()) {
            //Bukkit.broadcastMessage("spawned signed:");
            //printSign(spawnedSign.getLines());

          //  if (Arrays.equals(spawnedSign.getLines(), sign.getLines()))  {

                //Bukkit.broadcastMessage("i equal sign!");
            //}
        //}
    }

    private void printSign(String[] signText) {
        if (signText == null) {
            return;
        }

        for (String line : signText) {
            Bukkit.broadcastMessage(line);
        }
    }
}
