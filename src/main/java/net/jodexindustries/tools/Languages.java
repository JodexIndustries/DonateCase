package net.jodexindustries.tools;

import org.bukkit.configuration.file.YamlConfiguration;

import net.jodexindustries.dc.DonateCase;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

public class Languages
{
    public FileConfiguration lang;
    
    public Languages(final String lang) 
    {
        final File path = new File(DonateCase.instance.getDataFolder(), "lang");
        File[] listFiles;
        for (int length = (listFiles = path.listFiles()).length, i = 0; i < length; ++i) 
        {
            final File l = listFiles[i];
            if (l.getName().toLowerCase().split("_")[0].equalsIgnoreCase(lang)) 
            {
                this.lang = (FileConfiguration)YamlConfiguration.loadConfiguration(l);
            }
        }
    }
    
    public FileConfiguration getLang() 
    {
        return this.lang;
    }
}
