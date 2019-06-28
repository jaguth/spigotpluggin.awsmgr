package com.jaguth.spigotpluggin.awsmgr;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;

import java.util.concurrent.ThreadLocalRandom;

public class MinecraftUtil {

    enum EntityTypes {
        bat("bat", Bat.class),
        cat("cat", Cat.class),
        cod("cod", Cod.class),
        chicken("chicken", Chicken.class),
        cow("cow", Cow.class),
        donkey("donkey", Donkey.class),
        dolphin("dolphin", Dolphin.class),
        fish("fish", Fish.class),
        fox("fox", Fox.class),
        horse("horse", Horse.class),
        llama("llama", Llama.class),
        mule("mule", Mule.class),
        ocelot("ocelot", Ocelot.class),
        pando("pando", Panda.class),
        parrot("parrot", Parrot.class),
        pig("pig", Pig.class),
        polarbear("polarbear", PolarBear.class),
        pufferfish("pufferfish", PufferFish.class),
        rabbit("rabbit", Rabbit.class),
        salmon("salmon", Salmon.class),
        silverfish("silverfish", Silverfish.class),
        sheep("sheep", Sheep.class),
        squid("squid", Squid.class),
        tropicalfish("tropicalfish", TropicalFish.class),
        turtle("turtle", Turtle.class),
        wolf("wolf", Wolf.class);

        private final String entityName;
        private final Class entityClass;

        EntityTypes(String entityName, Class entityClass) {
            this.entityName = entityName;
            this.entityClass = entityClass;
        }

        private static Class getEntityClass(String entityName) throws Exception {
            if ("random".equals(entityName.toLowerCase().trim())) {
                int randomNumber = generateRandomInt(0, EntityTypes.values().length);
                return EntityTypes.values()[randomNumber].entityClass;
            }

            for (EntityTypes entityType : EntityTypes.values()) {
                if (entityType.entityName.equals(entityName.toLowerCase().trim())) {
                    return entityType.entityClass;
                }
            }

            throw new Exception("Entity \"" + entityName + "\" not found");
        }

        private static int generateRandomInt(int minRange, int maxRange) {
            return ThreadLocalRandom.current().nextInt(minRange, maxRange);
        }
    }

    public static Entity spawnEntityFromText(String entityName, String tagText, Player player) throws Exception {
        World world = player.getWorld();
        Location location = Bukkit.getEntity(player.getUniqueId()).getLocation();
        Class entityClass = EntityTypes.getEntityClass(entityName);
        Entity entity = world.spawn(location, entityClass);
        entity.setCustomName(tagText);
        entity.setCustomNameVisible(true);
        world.playEffect(entity.getLocation(), Effect.SMOKE, 50, 10);

        return entity;
    }

    private static int generateRandomInt(int minRange, int maxRange) {
        return ThreadLocalRandom.current().nextInt(minRange, maxRange);
    }

    public static Block spawnBlockWherePlayerLooking(Player player) {
        final int maxRange = 100;
        World world = player.getWorld();

        Block targetBlock = player.getTargetBlock(null, maxRange);
        Block highestBlock = world.getHighestBlockAt(targetBlock.getX(), targetBlock.getZ());
        highestBlock.setType(Material.DIRT);

        return highestBlock;
    }

    public static Block spawnSignWherePlayerLooking(Player player) {
        final int maxRange = 100;
        World world = player.getWorld();

        Block targetBlock = player.getTargetBlock(null, maxRange);
        Block highestBlock = world.getHighestBlockAt(targetBlock.getX(), targetBlock.getZ());
        highestBlock.setType(Material.OAK_SIGN);

        Sign sign = (Sign) highestBlock.getState();
        sign.setLine(0, "test");

        sign.update();

        return highestBlock;
    }

    public static BlockFace getPlayerDirection(Player player) {
        BlockFace dir = null;

        float y = player.getLocation().getYaw();

        if( y < 0 ) {
            y += 360;
        }

        y %= 360;

        int i = (int)((y+8) / 22.5);

        if (i == 0) {dir = BlockFace.WEST;}
        else if (i == 1) { dir = BlockFace.WEST_NORTH_WEST;}
        else if (i == 2) { dir = BlockFace.NORTH_WEST;}
        else if (i == 3) { dir = BlockFace.NORTH_NORTH_WEST;}
        else if (i == 4) { dir = BlockFace.NORTH;}
        else if (i == 5) { dir = BlockFace.NORTH_NORTH_EAST;}
        else if (i == 6) { dir = BlockFace.NORTH_EAST;}
        else if (i == 7) { dir = BlockFace.EAST_NORTH_EAST;}
        else if (i == 8) { dir = BlockFace.EAST;}
        else if (i == 9) { dir = BlockFace.EAST_SOUTH_EAST;}
        else if (i == 10 ){ dir = BlockFace.SOUTH_EAST;}
        else if (i == 11 ){ dir = BlockFace.SOUTH_SOUTH_EAST;}
        else if (i == 12 ){ dir = BlockFace.SOUTH;}
        else if (i == 13 ){ dir = BlockFace.SOUTH_SOUTH_WEST;}
        else if (i == 14 ){ dir = BlockFace.SOUTH_WEST;}
        else if (i == 15 ){ dir = BlockFace.WEST_SOUTH_WEST;}
        else {dir = BlockFace.WEST;}

        return dir;
    }
}
