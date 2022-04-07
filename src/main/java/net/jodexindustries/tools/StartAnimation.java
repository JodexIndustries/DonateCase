package net.jodexindustries.tools;

import org.bukkit.plugin.Plugin;
import java.util.Iterator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import net.jodexindustries.dc.Case;
import net.jodexindustries.dc.DonateCase;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import java.util.List;

public class StartAnimation
{
    public static List<Player> caseOpen = new ArrayList<Player>();
    
    public StartAnimation(final Player player, final Location location, final Case c) 
    {
        final Location lAC = location.clone();
        DonateCase.ActiveCase.put(lAC, c);
        StartAnimation.caseOpen.add(player);
        for (final Player pl : Bukkit.getOnlinePlayers()) 
        {
            if (DonateCase.openCase.containsKey(pl) && DonateCase.t.isHere(location, DonateCase.openCase.get(pl))) 
            {
                pl.closeInventory();
            }
        }
        final Case.ItemCase winGroup = DonateCase.t.getRandomGroup(c.getItems());
        location.add(0.5, -0.1, 0.5);
        location.setYaw(-70.0f);
        final ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        DonateCase.listAR.add(as);
        as.setGravity(false);
        as.setSmall(true);
        as.setVisible(false);
        as.setCustomNameVisible(true);
        new BukkitRunnable() 
        {
            int i;
            double t;
            Location l;
            
            public void run() 
            {
                final ItemStack winItem = winGroup.getItem();
                if (this.i == 0) 
                {
                    this.l = as.getLocation();
                }
                if (this.i >= 7) 
                {
                    if (this.i == 16) 
                    {
                        as.setHelmet(winItem);
                        as.setCustomName(winItem.getItemMeta().getDisplayName());
                        DonateCase.t.launchFirework(this.l.clone().add(0.0, 0.8, 0.0));
                        player.sendTitle(DonateCase.t.rc("&aПоздравляем!"), DonateCase.t.rc("&5вы выиграли "+winGroup.getDisplayName()), 5, 60, 5);
                        if (DonateCase.t.hasLevelGroup(player, winGroup.getGroup())) 
                        {
                            for (final String cmd : c.getCmds()) 
                            {
                                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), DonateCase.t.rt(cmd, "%player:" + player.getName(), "%group:" + winGroup.getGroup()));
                            }
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1f, 5f);
                        }
                        for (final Player pl : Bukkit.getOnlinePlayers()) 
                        {
                            for (final String cmd2 : DonateCase.lang.getStringList("GiveDonat")) 
                            {
                                DonateCase.t.msg_((CommandSender)pl, 
                                DonateCase.t.rt(cmd2, "%player:" + player.getName(), "%group:" + winGroup.getDisplayName(), "%case:" + c.getTitle()));
                            }
                        }
                    }
                    if (this.i >= 40) 
                    {
                        as.remove();
                        this.cancel();
                        DonateCase.ActiveCase.remove(lAC);
                        DonateCase.listAR.remove(as);
                        StartAnimation.caseOpen.remove(player);
                    }
                }
                if (this.i <= 15)
                {
                    final ItemStack rItem = DonateCase.t.getRandomGroup(c.getItems()).getItem();
                    as.setHelmet(rItem);
                    as.setCustomName(rItem.getItemMeta().getDisplayName());
                    if (this.i <= 8) 
                    {
                        final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.ORANGE, 1.0f);
                        this.l.getWorld().spawnParticle(Particle.REDSTONE, this.l.clone().add(0.0, 0.4, 0.0), 5, 0.3, 0.3, 0.3, 0.0, (Object)dustOptions);
                    }
                }
                final Location las = as.getLocation().clone();
                las.setYaw(las.getYaw() + 20.0f);
                as.teleport(las);
                this.l = this.l.add(0.0, 0.14, 0.0);
                if (this.i <= 7) 
                {
                    this.l.setYaw(las.getYaw());
                    as.teleport(this.l);
                }
                if (this.i <= 15)
                {
                    final double pi = 3.141592653589793;
                    this.t += 0.241660973353061;
                    Location loc = this.l.clone();
                    loc = loc.add(0.0, 0.6000000000000001, 0.0);
                    for (double phi = 0.0; phi <= 9.42477796076938; phi += 1.0471975511965976) 
                    {
                        final double x = 0.09 * (9.42477796076938 - this.t * 2.5) * Math.cos(this.t + phi);
                        final double z = 0.09 * (9.42477796076938 - this.t * 2.5) * Math.sin(this.t + phi);
                        loc.add(x, 0.0, z);
                        this.l.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, this.l.clone().add(0.0, 0.4, 0.0), 1, 0.1, 0.1, 0.1, 0.0);                
                        loc.subtract(x, 0.0, z);
                        if (this.t >= 21.991148575128552) 
                        {
                            loc.add(x, 0.0, z);
                            this.t = 0.0;
                        }
                    }
                }
                ++this.i;
            }
        }.runTaskTimer(DonateCase.instance, 0, 2);
    }
}