package net.jodexindustries.tools;

import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;

import net.jodexindustries.dc.DonateCase;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

public class CustomConfig
{
    FileConfiguration config;
    File file;
    
    public CustomConfig(final String name) 
    {
        this.file = new File(DonateCase.instance.getDataFolder(), String.valueOf(String.valueOf(String.valueOf(name))) + ".yml");
        this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.file);
    }
    
    public FileConfiguration getConfig() 
    {
        return this.config;
    }
    
    public void save() 
    {
        try{
            this.config.save(this.file);
        }catch (IOException var2){
            var2.printStackTrace();
        }
    }
}
