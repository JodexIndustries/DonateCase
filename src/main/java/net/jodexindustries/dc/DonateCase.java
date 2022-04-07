package net.jodexindustries.dc;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;
import org.bukkit.entity.ArmorStand;
import java.util.List;
import net.milkbowl.vault.permission.Permission;
import net.jodexindustries.commands.MainCommand;
import net.jodexindustries.listener.EventsListener;
import net.jodexindustries.tools.CustomConfig;
import net.jodexindustries.tools.Languages;
import net.jodexindustries.tools.Tools;

import org.bukkit.plugin.java.JavaPlugin;

public class DonateCase extends JavaPlugin
{
	public static DonateCase instance;
	
	public static Permission permission = null;
	public static boolean Tconfig = true;
	public static boolean LevelGroup = true;
	public static List<ArmorStand> listAR = new ArrayList<ArmorStand>();
	public static HashMap<Player, Location> openCase = new HashMap<Player, Location>();
	public static HashMap<Location, Case> ActiveCase = new HashMap<Location, Case>();
	public static HashMap<String, Integer> levelGroup = new HashMap<String, Integer>();
	public static Tools t;
	public static FileConfiguration lang;
	public static FileConfiguration config;
	public static CustomConfig Ckeys;
	public static CustomConfig CCase;
	public static MySQL mysql;
	public static String[] title = new String[2];
	
	private final PluginManager pluginManager = getServer().getPluginManager();
	private final String pluginVersion = getDescription().getVersion();

	public DonateCase() 
	{
		DonateCase.instance = this;
	}
	
	public void onEnable()
	{
		this.checkVersion();
		this.checkUpdate();
		this.loadMetrics();
		DonateCase.t = new Tools();
		this.saveDefaultConfig();
		DonateCase.config = this.getConfig();
		Bukkit.getPluginManager().registerEvents((Listener)new EventsListener(), (Plugin)this);
		if (!new File(this.getDataFolder(), "lang/ru_RU.yml").exists()) 
		{
			this.saveResource("lang/ru_RU.yml", false);
		}
		DonateCase.Ckeys = new CustomConfig("Keys");
		DonateCase.CCase = new CustomConfig("Cases");
		DonateCase.lang = new Languages(DonateCase.config.getString("DonatCase.Languages")).getLang();
		DonateCase.Tconfig = DonateCase.config.getString("DonatCase.TypeSave").equalsIgnoreCase("config");
		DonateCase.title[0] = DonateCase.config.getString("DonatCase.Title.Title");
		DonateCase.title[1] = DonateCase.config.getString("DonatCase.Title.SubTitle");
		DonateCase.LevelGroup = DonateCase.config.getBoolean("DonatCase.LevelGroup");
		this.setupPermissions();
		if (!DonateCase.Tconfig) 
		{
			final String host = DonateCase.config.getString("DonatCase.MySql.Host");
			final String user = DonateCase.config.getString("DonatCase.MySql.User");
			final String password = DonateCase.config.getString("DonatCase.MySql.Password");
			new BukkitRunnable() 
			{
				public void run() 
				{
					DonateCase.mysql = new MySQL(host, user, password);
					if (!DonateCase.mysql.hasTable("donate_cases")) 
					{
						DonateCase.mysql.createTable();
					}
				}
			}.runTaskTimer((Plugin)this, 0L, 12000L);
		}
		final ConfigurationSection cslg;
		if ((cslg = DonateCase.config.getConfigurationSection("DonatCase.LevelsGroup")) != null) 
		{
			for (final Map.Entry<?, ?> s : cslg.getValues(false).entrySet()) 
			{
				DonateCase.levelGroup.put(((String)s.getKey()).toLowerCase(), (Integer)s.getValue());
			}
		}
		final ConfigurationSection cases_;
		if ((cases_ = DonateCase.config.getConfigurationSection("DonatCase.Cases")) != null) 
		{
			for (final String cn : cases_.getValues(false).keySet()) {
				final String title = DonateCase.config.getString("DonatCase.Cases." + cn + ".Title");
				final Case c = new Case(cn, title);
				for (final String i : DonateCase.config.getConfigurationSection("DonatCase.Cases." + cn + ".Items").getValues(false).keySet()) 
				{
					final int chance = DonateCase.config.getInt("DonatCase.Cases." + cn + ".Items." + i + ".Chance");
					final String id = DonateCase.config.getString("DonatCase.Cases." + cn + ".Items." + i + ".Item.ID");
					final String displayname = DonateCase.config.getString("DonatCase.Cases." + cn + ".Items." + i + ".Item.DisplayName");
					final String group = DonateCase.config.getString("DonatCase.Cases." + cn + ".Items." + i + ".Group");
					c.setCmds(DonateCase.config.getStringList("DonatCase.Cases." + cn + ".Commands"));
					c.addItem(new Case.ItemCase(i, chance, id, group, displayname));
				}
			}
		}
		final FileConfiguration fckeys = DonateCase.Ckeys.getConfig();
		final ConfigurationSection csc;
		if (DonateCase.Tconfig && (csc = fckeys.getConfigurationSection("DonatCase.Cases")) != null) 
		{
			for (final String s2 : csc.getValues(false).keySet()) 
			{
				if (Case.hasCaseByName(s2)) 
				{
					final Case c2 = Case.getCaseByName(s2);
					final ConfigurationSection csk = fckeys.getConfigurationSection("DonatCase.Cases." + s2);
					if (csk == null) 
					{
						continue;
					}
					for (final Map.Entry<?, ?> k : csk.getValues(false).entrySet()) 
					{
						c2.setKeys((String)k.getKey(), (int)k.getValue());
					}
				}
			}
		}
		final FileConfiguration fccase;
		final ConfigurationSection cslc;
		if ((cslc = (fccase = DonateCase.CCase.getConfig()).getConfigurationSection("DonatCase.Cases")) != null) 
		{
			for (final String s3 : cslc.getValues(false).keySet()) 
			{
				if (Case.hasCaseByName(s3)) 
				{
					final Case c3 = Case.getCaseByName(s3);
					for (final String lc : fccase.getStringList("DonatCase.Cases." + s3 + ".Case")) 
					{
						c3.getLocation().add(DonateCase.t.getLoc(lc));
					}
				}
			}
		}
		this.getCommand("donatcase").setExecutor(new MainCommand("donatcase"));
	}

	public void onDisable() 
	{
		for (final ArmorStand as : DonateCase.listAR) 
		{
			if (as != null)
			{
				as.remove();
			}
		}
		if (DonateCase.mysql != null) 
		{
			DonateCase.mysql.close();
		}
	}
	private void checkVersion() 
	{
		final String javaVersion = System.getProperty("java.version");
		final int dotIndex = javaVersion.indexOf('.');
		final int endIndex = dotIndex == -1 ? javaVersion.length() : dotIndex;
		final String version = javaVersion.substring(0, endIndex);
		final int javaVersionNum;
		try{
			javaVersionNum = Integer.parseInt(version);
		}catch(final NumberFormatException e){
			Logger.getLogger(Level.WARNING + "Failed to determine Java version; Could not parse {}".replace("{}", version) + e);
			Logger.getLogger(Level.WARNING + String.valueOf(javaVersion));
			return;
		}
		String serverVersion;
		try{
			serverVersion = this.instance.getServer().getClass().getPackage().getName().split("\\.")[3];
		}catch(ArrayIndexOutOfBoundsException whatVersionAreYouUsingException){
			return;
		}
		Logger.getLogger(Level.INFO + "&6You are running is &ejava &6version: &e<javaVersion>".replace("<javaVersion>", String.valueOf(javaVersionNum)));
		Logger.getLogger(Level.INFO + "&6Your &eserver &6is running version: &e<serverVersion>".replace("<serverVersion>", String.valueOf(serverVersion)));
	}
	private void checkUpdate()
	{
		new UpdateChecker(this, 81333).getVersion(version -> 
		{
			if (this.getDescription().getVersion().equalsIgnoreCase(version)) 
			{	
				Logger.getLogger(Level.INFO + "&6==============================================");
				Logger.getLogger(Level.INFO + "Current version: &b<pl_ver>".replace("<pl_ver>", pluginVersion));
				Logger.getLogger(Level.INFO + "This is latest version plugin.".replace("<pl_ver>", pluginVersion));
				Logger.getLogger(Level.INFO + "&6==============================================");
			}else{
				Logger.getLogger(Level.INFO + "&6==============================================");
				Logger.getLogger(Level.INFO + "&eThere is a new version update available.");
				Logger.getLogger(Level.INFO + "&cCurrent version: &4<pl_ver>".replace("<pl_ver>", pluginVersion));
				Logger.getLogger(Level.INFO + "&3New version: &b<new_pl_ver>".replace("<new_pl_ver>", version));
				Logger.getLogger(Level.INFO + "&ePlease download new version here:");
				Logger.getLogger(Level.INFO + "&ehttps://www.spigotmc.org/resources/serverregionprotect-1-13-1-17.81321/");
				Logger.getLogger(Level.INFO + "&6==============================================");
			}
		});
	}
	private void loadMetrics()
	{
		int pluginId = 12963;
		Metrics metrics = new Metrics(this, pluginId);
	}

	private boolean setupPermissions() {
		final RegisteredServiceProvider<Permission> permissionProvider = (RegisteredServiceProvider<Permission>)this.getServer().getServicesManager().getRegistration((Class)Permission.class);
		if (permissionProvider != null) {
			DonateCase.permission = (Permission)permissionProvider.getProvider();
		}
		return DonateCase.permission != null;
	}
}
