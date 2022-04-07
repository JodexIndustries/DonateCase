package net.jodexindustries.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.util.Set;

import org.bukkit.entity.Player;

import net.jodexindustries.commands.executor.DCCommand;
import net.jodexindustries.dc.Case;
import net.jodexindustries.dc.DonateCase;

import org.bukkit.configuration.ConfigurationSection;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class MainCommand extends DCCommand
{
	//private final List<String> subCommand = Arrays.asList("help", "create", "delete", "givekey", "setkey");
	
    public MainCommand(String name) 
    {
		super("donatcase");
	}
    
     public void run(CommandSender sender, Command cmd, String label, String[] args) throws Exception 
     {
        if (sender.hasPermission("DonatCase.Admin")) 
        {
            if (args.length >= 1) 
            {
                if (!args[0].equalsIgnoreCase("create") && (!args[0].equalsIgnoreCase("delete") || !(sender instanceof Player))) 
                {
                    if (args[0].equalsIgnoreCase("givekey")) 
                    {
                        if (args.length >= 4) {
                            try {
                                final String player = args[1];
                                final String nCase = args[2];
                                final int keys = Integer.parseInt(args[3]);
                                if (Case.hasCaseByName(nCase)) 
                                {
                                    final Case ca = Case.getCaseByName(nCase);
                                    ca.addKeys(player, keys);
                                    DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("GiveKeys"), "%player:" + player, "%key:" + keys, "%case:" + nCase, "%ending:" + DonateCase.t.getEnding(keys, "\u044f", "\u0435\u0439")));
                                }else{
                                    DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("CaseNotExist"), "%name:" + nCase));
                                }
                            }catch(Exception var11){
                                this.help(sender, label);
                            }
                        }else{
                            this.help(sender, label);
                        }
                    }else if (args[0].equalsIgnoreCase("setkey")) 
                    {
                        if (args.length >= 4) 
                        {
                            try{
                                final String player = args[1];
                                final String nCase = args[2];
                                final int keys = Integer.parseInt(args[3]);
                                if (Case.hasCaseByName(nCase)) 
                                {
                                    final Case ca = Case.getCaseByName(nCase);
                                    ca.setKeys(player, keys);
                                    DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("SetKeys"), "%player:" + player, "%key:" + keys, "%case:" + nCase, "%ending:" + DonateCase.t.getEnding(keys, "\u044f", "\u0435\u0439")));
                                }else{
                                    DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("CaseNotExist"), "%name:" + nCase));
                                }
                            }catch (Exception var12){
                                this.help(sender, label);
                            }
                        }else{
                            this.help(sender, label);
                        }
                    }else if (args[0].equalsIgnoreCase("help"))
                    {
                        for (final String line : DonateCase.lang.getStringList("Help")) 
                        {
                            DonateCase.t.msg_(sender, DonateCase.t.rt(line, "%cmd:" + label));
                        }
                    }else{
                        this.help(sender, label);
                    }
                }else{
                    final boolean cr = args[0].substring(0, 1).equals("c");
                    final Player p = (Player)sender;
                    final Location l = p.getTargetBlock((Set)null, 5).getLocation();
                    if (cr)
                    {
                        if (args.length >= 2)
                        {
                            final String name = args[1];
                            if (Case.hasCaseByName(name)) 
                            {
                                final Case ca2 = Case.getCaseByName(name);
                                if (Case.hasCaseByLocation(l)) 
                                {
                                    DonateCase.t.msg(sender, DonateCase.lang.getString("HasDonatCase"));
                                }else{
                                    ca2.addLocation(l);
                                    DonateCase.t.msg(sender, DonateCase.lang.getString("AddDonatCase"));
                                }
                            }else{
                                DonateCase.t.msg(sender, DonateCase.t.rt(DonateCase.lang.getString("CaseNotExist"), "%name:" + name));
                            }
                        }else{
                            this.help(sender, label);
                        }
                    }else if (Case.hasCaseByLocation(l))
                    {
                        final Case cs = Case.getCaseByLocation(l);
                        cs.removeLocation(l);
                        DonateCase.t.msg(sender, DonateCase.lang.getString("RemoveDonatCase"));
                    }else{
                        DonateCase.t.msg(sender, DonateCase.lang.getString("BlockDontDonatCase"));
                    }
                }
            }else{
                this.help(sender, label);
            }
        }else{
            DonateCase.t.msg(sender, DonateCase.lang.getString("NoPermission"));
        }
        return;
    }
    
	@Override
	public List<String> onTabComplete(CommandSender sender, Player $p, Command $cmd, String $label, String[] args) 
	{
        if (args.length == 1 && sender.hasPermission("*")) 
        {
            ArrayList<String> list = new ArrayList<String>();
            final ArrayList<String> value = new ArrayList<String>();
            value.add("help");
            value.add("create");
            value.add("delete");
            value.add("givekey");
            value.add("setkey");
            if (args[0].equals(""))
            {
                list = value;
            }else{
                for (final String tmp : value) 
                {
                    if (tmp.startsWith(args[0].toLowerCase())) 
                    {
                        list.add(tmp);
                    }
                }
            }
            Collections.sort(list);
            System.out.println(list);
            return list;
        }
        if (args.length == 2 && sender.hasPermission("*") && args[0].equalsIgnoreCase("create")) 
        {
            ArrayList<String> list = new ArrayList<String>();
            final ArrayList<String> value = new ArrayList<String>();
            final ConfigurationSection section = DonateCase.config.getConfigurationSection("DonatCase.Cases");
            for (final String tmp2 : section.getKeys(false)) 
            {
                value.add(tmp2.toLowerCase());
            }
            if (args[1].equals("")) 
            {
                list = value;
            }else{
                for (final String tmp2 : value)
                {
                    if (tmp2.startsWith(args[1].toLowerCase())) 
                    {
                        list.add(tmp2.toLowerCase());
                    }
                }
            }
            Collections.sort(list);
            return list;
        }
        if (args.length == 3 && sender.hasPermission("*") && (args[0].equalsIgnoreCase("givekey") || args[0].equalsIgnoreCase("setkey")))
        {
            ArrayList<String> list = new ArrayList<String>();
            final ArrayList<String> value = new ArrayList<String>();
            final ConfigurationSection section = DonateCase.config.getConfigurationSection("DonatCase.Cases");
            for (final String tmp2 : section.getKeys(false))
            {
                value.add(tmp2.toLowerCase());
            }
            if (args[2].equals(""))
            {
                list = value;
            }else{
                for (final String tmp2 : value)
                {
                    if (tmp2.startsWith(args[2].toLowerCase())) 
                    {
                        list.add(tmp2.toLowerCase());
                    }
                }
            }
            Collections.sort(list);
            return list;
        }
        return new ArrayList<>();
    }
    
    public void help(final CommandSender s, final String cmd) 
    {
        Bukkit.getServer().dispatchCommand(s, DonateCase.t.rt("%cmd help", "%cmd:" + cmd));
    }
}