package me.wither.waterkiri;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;


public class Respiration extends WaterAbility implements AddonAbility {
    private static long COOLDOWN;
    private static long DURATION;
    private static boolean HUNGER;
    private static int BUBBLE_AMOUNT;

    public void setField() {
        COOLDOWN = ConfigManager.getConfig().getLong("ExtraAbilities.Sc1_original.Respiration.Cooldown");
        DURATION = ConfigManager.getConfig().getLong("ExtraAbilities.Sc1_original.Respiration.Duration");
        HUNGER = ConfigManager.getConfig().getBoolean("ExtraAbilities.Sc1_original.Respiration.Hunger");
        BUBBLE_AMOUNT = ConfigManager.getConfig().getInt("ExtraAbilities.Sc1_original.Respiration.Bubble Amount");
    }
    private long time;
    private Listener listener;
    private Permission perm;
    private Location location;
    public Respiration(Player player) {
        super(player);
        bPlayer.addCooldown(this);

        setField();
        start();
    }

    @Override
    public void progress() {
        this.time = System.currentTimeMillis();
        if(!bPlayer.canBendIgnoreBindsCooldowns(this)){
            remove();
            return;

        }
        location = player.getLocation();

        if(this.time > getStartTime() + DURATION){
            remove();
            return;
        }


        if(location.getBlock().getType().isSolid()){
            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
            player.removePotionEffect(PotionEffectType.HUNGER);
            remove();
            if(bPlayer.isOnCooldown(this)) {
                bPlayer.removeCooldown(this);
            }


        }
        if(!player.isInWater()){
            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
            player.removePotionEffect(PotionEffectType.HUNGER);
            if(bPlayer.isOnCooldown(this)) {
                bPlayer.removeCooldown(this);
            }
            remove();
            return;
        }
        if(player.isInWater()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 , 1));
            if(HUNGER){
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER , 20 , 1));
            }

        }


        ParticleEffect.WATER_BUBBLE.display(location, BUBBLE_AMOUNT , 0.6 , 0.9 , 0.6);
        if(player.isInWater()){
            if (player.isSwimming()){
                ParticleEffect.WATER_BUBBLE.display(location, BUBBLE_AMOUNT , 0.9 , 0.6 , 0.8);
            }
            else {
                ParticleEffect.WATER_BUBBLE.display(location, BUBBLE_AMOUNT , 0.6 , 1.3 , 0.6);
            }
        }

    }


    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return true;
    }

    @Override
    public long getCooldown() {
        return COOLDOWN;
    }
    @Override
    public String getDescription() {
        return "Respiration is an ultimate sub-skill of waterbending, only achieved by true waterbending masters. It allows a waterbender to pull oxygen from the water surrounding them into their own bodies, enabling them to breathe underwater, the proccess makes you hungry!";

    }

    @Override
    public String getName() {
        return "Respiration";
    }

    @Override
    public Location getLocation() {
        return location;

    }

    @Override
    public void load() {
        listener = new RespirationListener();
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents(listener , ProjectKorra.plugin);
        perm = new Permission("bending.ability.respiration");
        perm.setDefault(PermissionDefault.OP);
        ProjectKorra.plugin.getServer().getPluginManager().addPermission(perm);

        ConfigManager.getConfig().addDefault("ExtraAbilities.Sc1_original.Respiration.Cooldown" , 18000L);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Sc1_original.Respiration.Duration" , 15000L);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Sc1_original.Respiration.Hunger" , true);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Sc1_original.Respiration.Bubble Amount" , 5);

    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(listener);
        ProjectKorra.plugin.getServer().getPluginManager().removePermission(perm);


    }

    @Override
    public String getAuthor() {
        return "sc1_original";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

}

