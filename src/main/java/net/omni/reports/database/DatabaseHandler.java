package net.omni.reports.database;

import net.omni.reports.ReportsPlugin;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.logging.Logger;

public class DatabaseHandler {

    private static final Logger logger = Bukkit.getLogger();
    private final ReportsPlugin plugin;
    private final String connectionString;
    private Connection c = null;

    public DatabaseHandler(String hostname, int port, String database, String username, String password, ReportsPlugin plugin) {
        this.connectionString = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?user=" + username + "&password="
                + password;
        this.plugin = plugin;
        plugin.sendConsole("&aDatabase: " + database);
        plugin.sendConsole("&aUser: " + username);
        plugin.sendConsole("&aPassword: " + password);
    }

    public Connection open() {
        String driver = "com.mysql.jdbc.Driver";
        try {
            Class.forName(driver);

            this.c = DriverManager.getConnection(connectionString);
            plugin.sendConsole("&aConnected to database!");
            return c;
        } catch (SQLException e) {
            System.out.println("Could not connect to Database! because: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(driver + " not found!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return this.c;
    }

    public Connection getConn() {
        return this.c;
    }

    public void close() {
        try {
            if (c != null)
                c.close();
        } catch (SQLException ex) {
            System.out.println("ERROR:" + ex.getMessage());
        }
        c = null;
    }

    public boolean isConnected() {
        try {
            return (c != null && !c.isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Result query(final String query) {
        if (!isConnected())
            open();
        return query(query, true);
    }

    public Result query(final String query, boolean retry) {
        if (!isConnected())
            open();

        try {
            PreparedStatement statement = null;

            try {
                if (!isConnected())
                    open();
                statement = c.prepareStatement(query);

                if (statement.execute())
                    return new Result(statement, statement.getResultSet());
            } catch (final SQLException e) {
                final String msg = e.getMessage();
                logger.severe("Database query error: " + msg);

                if (retry && msg.contains("_BUSY")) {
                    logger.severe("Retrying query...");
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                            () -> query(query, false), 20);
                }
            }

            if (statement != null)
                statement.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    public static class Result {
        private final ResultSet resultSet;
        private final Statement statement;

        public Result(Statement statement, ResultSet resultSet) {
            this.statement = statement;
            this.resultSet = resultSet;
        }

        public ResultSet getResultSet() {
            return this.resultSet;
        }

        public void close() {
            try {
                this.statement.close();
                this.resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}