package net.jodexindustries.commands.executor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.collect.Lists;

public abstract class DCCommand implements CommandExecutor, TabExecutor 
{
    private Player p;
    
    private final transient String name;
    
    public DCCommand(String name) 
    {
        this.name = name;
    }

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        try{
			run(sender,cmd,label,args);
		}catch(Exception e){
			e.printStackTrace();
		}
        return true;
    }
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) 
	{
		return onTabComplete(sender, p, cmd, label, args);
	}

	public abstract void run(CommandSender s, Command cmd, String label, String[] args) throws Exception;
	
	public abstract List<String> onTabComplete(CommandSender s, Player p, Command cmd, String label, String[] args);
}