package com.jaguth.spigotpluggin.awsmgr;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.concurrent.ThreadLocalRandom;

public class MinecraftUtil {

    public static final Class[] entityTypes = new Class[]{
            Chicken.class,
            Cow.class,
            Donkey.class,
            Horse.class,
            Llama.class,
            Ocelot.class,
            Parrot.class,
            PolarBear.class,
            Rabbit.class,
            Sheep.class,
            Turtle.class,
            Wolf.class
    };

    public static Entity spawnEntityFromText(String entityName, String tagText, Player player) throws Exception {
        World world = player.getWorld();
        Location location = Bukkit.getEntity(player.getUniqueId()).getLocation();
        Class entityClass = getEntityClassFromText(entityName);
        Entity entity = world.spawn(location, entityClass);
        entity.setCustomName(tagText);
        entity.setCustomNameVisible(true);
        world.playEffect(entity.getLocation(), Effect.SMOKE, 50, 10);

        return entity;
    }

    private static Class getEntityClassFromText(String entityName) throws Exception {
        switch (entityName) {
            case "bat":
                return Bat.class;
            case "chicken":
                return Chicken.class;
            case "cow":
                return Cow.class;
            case "donkey":
                return Donkey.class;
            case "horse":
                return Horse.class;
            case "llama":
                return Llama.class;
            case "ocelot":
                return Ocelot.class;
            case "parrot":
                return Parrot.class;
            case "polarbear":
                return PolarBear.class;
            case "rabbit":
                return Rabbit.class;
            case "sheep":
                return Sheep.class;
            case "turtle":
                return Turtle.class;
            case "wolf":
                return Wolf.class;
            case "random":
                int randomNumber = generateRandomInt(0, entityTypes.length);
                return entityTypes[randomNumber];
            default:
                throw new Exception("Entity name \"" + entityName + "\" not found.");
        }
    }

    private static int generateRandomInt(int minRange, int maxRange) {
        return ThreadLocalRandom.current().nextInt(minRange, maxRange);
    }
}
