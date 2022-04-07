package net.jodexindustries.dc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;

public class MySQL
{
    public Connection con;
    public Statement stmt;
    
    public MySQL(final String host, final String user, final String password) {
        try {
            if (this.con != null) {
                this.con.close();
            }
            this.con = DriverManager.getConnection(host, user, password);
            this.stmt = this.con.createStatement();
        }
        catch (SQLException var5) {
            var5.printStackTrace();
            Bukkit.getPluginManager().disablePlugin((Plugin)DonateCase.instance);
        }
    }
    
    public int getKey(final String name, String player) {
        try {
            player = player.toLowerCase();
            final ResultSet rs = this.stmt.executeQuery("SELECT * FROM `donate_cases` WHERE `player`='" + player + "' AND case_name='" + name + "'");
            if (rs.next()) {
                return rs.getInt(3);
            }
        }
        catch (SQLException var4) {
            var4.printStackTrace();
        }
        return 0;
    }
    
    public void createTable() {
        try {
            this.stmt.executeUpdate("CREATE TABLE `donate_cases` (`player` varchar(16) NOT NULL, `case_name` varchar(32) NOT NULL, `keys_count` int(16) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1");
        }
        catch (SQLException var2) {
            var2.printStackTrace();
        }
    }
    
    public void setKey(final String name, String player, final int keys) {
        try {
            player = player.toLowerCase();
            if (!this.hasField("donate_cases", "player='" + player + "' AND case_name='" + name + "'")) {
                this.stmt.executeUpdate(DonateCase.t.rt("INSERT INTO `donate_cases` (`player`, `case_name`, `keys_count`) VALUES ('%player', '%case', '%keys')", "%player:" + player, "%keys:" + keys, "%case:" + name));
            }
            else {
                this.stmt.executeUpdate(DonateCase.t.rt("UPDATE `donate_cases` SET keys_count='%keys' WHERE player='%player' AND case_name='%case'", "%player:" + player, "%keys:" + keys, "%case:" + name));
            }
        }
        catch (SQLException var5) {
            var5.printStackTrace();
        }
    }
    
    public boolean hasTable(final String table) {
        try {
            this.stmt.executeQuery("SELECT * FROM " + table);
            return true;
        }
        catch (SQLException var3) {
            return false;
        }
    }
    
    public boolean hasField(final String table, final String t) {
        try {
            final ResultSet rs = this.stmt.executeQuery("SELECT * FROM `" + table + "` WHERE " + t);
            rs.next();
            rs.getString(1);
            return true;
        }
        catch (SQLException var4) {
            return false;
        }
    }
    
    public void close() {
        try {
            this.con.close();
        }
        catch (SQLException var2) {
            var2.printStackTrace();
        }
    }
}
