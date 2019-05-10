package com.jaguth.spigotpluggin.awsmgr;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.concurrent.ThreadLocalRandom;

public class MinecraftUtil {

    enum EntityTypes {
        //bat("bat", Bat.class),
        chicken("chicken", Chicken.class),
        cow("cow", Cow.class),
        donkey("donkey", Donkey.class),
        horse("horse", Horse.class),
        llama("llama", Llama.class),
        ocelot("ocelot", Ocelot.class),
        //parrot("parrot", Parrot.class),
        polarbear("polarbear", PolarBear.class),
        rabbit("rabbit", Rabbit.class),
        sheep("sheep", Sheep.class),
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
}
