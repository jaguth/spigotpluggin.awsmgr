package com.jaguth.spigotpluggin.awsmgr;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MinecraftUtil {

    public enum EntityTypes {
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

        public static Class getEntityClass(String entityName) throws Exception {
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

        public static List<String> getNameList() {
            List<String> nameList = new ArrayList<>();

            for (EntityTypes entityType : EntityTypes.values()) {
                nameList.add(entityType.entityName);
            }

            nameList.add("random");

            return nameList;
        }

        private static int generateRandomInt(int minRange, int maxRange) {
            return ThreadLocalRandom.current().nextInt(minRange, maxRange);
        }

        public static boolean contains(String search) {
            try {
                getEntityClass(search);
                return true;
            }
            catch (Exception e) { // while i disagree with developing to exceptions, the user will get feedback and should not spam this
                return false;
            }
        }
    }

    public static Entity spawnEntityAtPlayerLocation(String entityType, String tagText, Player player) throws Exception {
        World world = player.getWorld();
        Location location = Bukkit.getEntity(player.getUniqueId()).getLocation();
        Class entityClass = EntityTypes.getEntityClass(entityType);
        Entity entity = world.spawn(location, entityClass);
        entity.setCustomName(tagText);
        entity.setCustomNameVisible(true);
        world.playEffect(entity.getLocation(), Effect.SMOKE, 50, 10);

        return entity;
    }

    public static Entity spawnEntityAtBlockLocation(String entityType, String tagText, Block block) throws Exception {
        World world = block.getWorld();
        Class entityClass = EntityTypes.getEntityClass(entityType);
        Entity entity = world.spawn(block.getLocation(), entityClass);
        entity.setCustomName(tagText);
        entity.setCustomNameVisible(true);
        world.playEffect(entity.getLocation(), Effect.SMOKE, 50, 10);

        return entity;
    }

    public static Block spawnSignWherePlayerLooking(Player player, String[] signText) {
        final int maxRange = 100;
        World world = player.getWorld();

        Block targetBlock = player.getTargetBlock(null, maxRange);
        Block highestBlock = world.getHighestBlockAt(targetBlock.getX(), targetBlock.getZ());
        highestBlock.setType(Material.OAK_SIGN);

        Sign sign = (Sign) highestBlock.getState();

        if (signText.length > 0) {
            for (int i = 0; i < signText.length && i < 4; i++) {
                sign.setLine(0, signText[i]);
            }
        }

        BlockFace oppositeFace = player.getFacing().getOppositeFace();
        BlockData signBlockData = highestBlock.getBlockData();

        if (signBlockData instanceof Directional) {
            Directional directional = (Directional) signBlockData;
            directional.setFacing(oppositeFace);
        }

        return highestBlock;
    }
}
