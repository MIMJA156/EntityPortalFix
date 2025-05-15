package org.mimja.entityPortalFix;

import org.bukkit.Bukkit;
import org.bukkit.PortalType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public final class EntityPortalFix extends JavaPlugin implements Listener {
    Path monsterPath = Paths.get(String.valueOf(this.getDataPath().toAbsolutePath()), "entities.json");
    MonsterBucket monsterBucket = new MonsterBucket(monsterPath);

    public static Logger log;

    @EventHandler
    public void onEntityPortalEvent(EntityPortalEvent event) {
        Entity entity = event.getEntity();

        if(event.getPortalType() == PortalType.NETHER && entity instanceof Monster monster) {
            monster.setRemoveWhenFarAway(false);
            monsterBucket.monsters.put(monster.getUniqueId(), new Date(new Date().getTime() + 60 * 1000));
            monsterBucket.save();
        }
    }

    @Override
    public void onEnable() {
        log = getLogger();
        log.info("EntityPortalFix is enabled - this is VERY important");
        Bukkit.getPluginManager().registerEvents(this, this);

        monsterBucket.load();

        new BukkitRunnable() {
            @Override
            public void run() {
                Vector<UUID> toRemove = new Vector<>();

                for(HashMap.Entry<UUID, Date> entry : monsterBucket.monsters.entrySet()) {
                    if(new Date().after(entry.getValue())) {
                        Monster monster = (Monster) Bukkit.getEntity(entry.getKey());
                        if(monster != null) monster.setRemoveWhenFarAway(true);
                        toRemove.add(entry.getKey());
                    }
                }

                for(UUID uuid : toRemove) monsterBucket.monsters.remove(uuid);
                if(!toRemove.isEmpty()) monsterBucket.save();
            }
        }.runTaskTimer(this, 0L,  20L);
    }

    @Override
    public void onDisable() {
        monsterBucket.save();
    }
}