package com.jaguth.spigotpluggin.awsmgr;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
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

}
