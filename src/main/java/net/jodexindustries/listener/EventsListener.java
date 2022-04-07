package net.jodexindustries.listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.jodexindustries.dc.Case;
import net.jodexindustries.dc.DonateCase;
import net.jodexindustries.gui.GuiDonatCase;
import net.jodexindustries.tools.StartAnimation;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.entity.Firework;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.Listener;

public class EventsListener implements Listener
{
    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) 
    {
        if (event.getDamager() instanceof Firework && event.getEntity() instanceof Player && event.getDamager().hasMetadata("case"))
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void InventoryClick(final InventoryClickEvent e)
    {
        if (e.getCurrentItem() != null) 
        {
            final Player p = (Player)e.getWhoClicked();
            final String pl = p.getName();
            final String title = e.getView().getTitle();
            if (Case.hasCaseByTitle(title)) 
            {
                e.setCancelled(true);
                if (e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getInventory().getType() == InventoryType.CHEST && e.getRawSlot() == DonateCase.t.c(5, 3)) {
                    final Case c = Case.getCaseByTitle(title);
                    if (c.getKeys(pl) >= 1) 
                    {
                        if (DonateCase.openCase.containsKey(p)) 
                        {
                            final Location block = DonateCase.openCase.get(p);
                            c.removeKeys(pl, 1);
                            new StartAnimation(p, block, c);
                        }
                        p.closeInventory();
                    }else{
                        p.closeInventory();
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.4f);
                        DonateCase.t.msg((CommandSender)p, DonateCase.lang.getString("NoKey"));
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void PlayerInteractEntity(final PlayerInteractAtEntityEvent e) 
    {
        final Entity entity = e.getRightClicked();
        if (entity.getType() == EntityType.ARMOR_STAND && DonateCase.listAR.contains(entity)) 
        {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void PlayerInteract(final PlayerInteractEvent e) 
    {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) 
        {
            final Player p = e.getPlayer();
            final Location loc = e.getClickedBlock().getLocation();
            if (Case.hasCaseByLocation(loc)) 
            {
                e.setCancelled(true);
                if (!StartAnimation.caseOpen.contains(p)) 
                {
                    if (!DonateCase.ActiveCase.containsKey(loc)) 
                    {
                        DonateCase.openCase.put(p, loc.clone());
                        new GuiDonatCase(p, Case.getCaseByLocation(loc));
                    }else{
                        DonateCase.t.msg((CommandSender)p, DonateCase.lang.getString("HaveOpenCase"));
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void InventoryClose(final InventoryCloseEvent e) 
    {
        final Player p = (Player)e.getPlayer();
        if (Case.hasCaseByTitle(e.getView().getTitle()) && DonateCase.openCase.containsKey(p)) 
        {
            DonateCase.openCase.remove(p);
        }
    }
    
    @EventHandler
    public void BlockBreak(final BlockBreakEvent e) 
    {
        final Location loc = e.getBlock().getLocation();
        if (Case.hasCaseByLocation(loc)) 
        {
            e.setCancelled(true);
            //DonateCase.t.msg((CommandSender)e.getPlayer(), DonateCase.lang.getString("DestoryDonatCase"));
        }
    }
}