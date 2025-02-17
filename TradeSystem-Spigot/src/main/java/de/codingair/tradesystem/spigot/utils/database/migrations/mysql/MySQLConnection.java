package de.codingair.tradesystem.spigot.utils.database.migrations.mysql;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.utils.Supplier;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    private static final ConfigFile file = TradeSystem.getInstance().getFileManager().getFile("Config");
    private static final FileConfiguration config = file.getConfig();
    private static MySQLConnection instance;

    private final String url;
    private final String user;
    private final String password;

    private MySQLConnection() {
        url = config.getString("TradeSystem.TradeLog.Database.MySQL.Connection_URL");
        user = config.getString("TradeSystem.TradeLog.Database.MySQL.User");

        String password = config.getString("TradeSystem.TradeLog.Database.MySQL.Password");
        if (password != null && password.equalsIgnoreCase("null")) password = null;

        this.password = password;
    }

    private static MySQLConnection getInstance() {
        if (instance == null) instance = new MySQLConnection();
        return instance;
    }

    public static Supplier<Connection, SQLException> getConnection() {
        return () -> getInstance().buildConnection();
    }

    public static void checkDataSource() throws SQLException {
        getConnection().get().close();
    }

    private Connection buildConnection() throws SQLException {
        if (url == null || user == null) return null;
        return DriverManager.getConnection(url, user, password);
    }
}
