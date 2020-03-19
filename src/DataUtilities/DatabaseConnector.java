package DataUtilities;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class to interface with a PostgreSQL database.
 */
public class DatabaseConnector {
    private String url;
    private String user;
    private String pw;
    private Connection conn;

    private static Logger logger = Logger.getLogger(TextLoader.class.getName());

    /**
     * Constructor to initialize DatabaseConnector.
     * @param url database url
     * @param user database user
     * @param pw database password
     */
    public DatabaseConnector(String url, String user, String pw) throws IllegalArgumentException {
        if (url == null) {
            throw new IllegalArgumentException("Invalid null argument for url.");
        } else if (user == null) {
            throw new IllegalArgumentException("Invalid null argument for user.");
        } else if (pw == null) {
            throw new IllegalArgumentException("Invalid null argument for pw.");
        }

        this.url = url;
        this.user = user;
        this.pw = pw;
        this.conn = null;
    }

    /**
     * Method that establishes database connection.
     */
    public void connect() {
        try {
            this.conn = DriverManager.getConnection(this.url, this.user, this.pw);
            logger.log(Level.FINE, "Successfully connected to database.");
        } catch (SQLException sqle) {
            logger.log(Level.SEVERE, String.format("Failed to connect with exception: %s", sqle));
        }
    }

    /**
     * Method that disconnects from the database.
     */
    public void disconnect() {
        try {
            this.conn.close();
        } catch (SQLException sqle) {
            logger.log(Level.SEVERE, String.format("Failed to disconnect with exception: %s", sqle));
        }
    }

    /**
     * Create a database.
     * @param databaseName name of database to create
     * @return 0 if successful, -1 otherwise
     */
    public int create_database(String databaseName) throws IllegalArgumentException {
        if (databaseName == null) {
            throw new IllegalArgumentException("Invalid null argument for databaseName.");
        }
        return updateDatabaseInternal(String.format("CREATE DATABASE %s;", databaseName),
                "Failed to create database with exception: ");
    }

    /**
     * Drop a database.
     * @param databaseName name of database to drop
     * @return 0 if successful, -1 otherwise
     */
    public int drop_database(String databaseName) throws IllegalArgumentException {
        if (databaseName == null) {
            throw new IllegalArgumentException("Invalid null argument for databaseName.");
        }
        return updateDatabaseInternal(String.format("DROP DATABASE %s;", databaseName),
                "Failed to drop database with exception: ");
    }

    /**
     * Create a database table.
     * @param tableName name of table to create
     * @param columns columns of table
     * @param datatypes datatypes for each column
     * @return 0 if successful, -1 otherwise
     */
    public int create_table(String tableName, String[] columns, String[] datatypes) throws IllegalArgumentException {
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid null argument for tableName.");
        } else if (columns == null) {
            throw new IllegalArgumentException("Invalid null argument for columns.");
        } else if (datatypes == null) {
            throw new IllegalArgumentException("Invalid null argument for datatypes.");
        } else if (columns.length != datatypes.length) {
            throw new IllegalArgumentException("Column and datatype array lengths do not match.");
        }

        StringBuilder colStrBuilder = new StringBuilder();
        colStrBuilder.append("(");
        for (int i = 0; i < columns.length; i++) {
            colStrBuilder.append(String.format("%s %s", columns[i], datatypes[i]));

            // handle last case
            if (i == columns.length - 1) {
                colStrBuilder.append(")");
            } else {
                colStrBuilder.append(",");
            }
        }

        return updateDatabaseInternal(
                String.format("CREATE TABLE %s %s;", tableName, colStrBuilder),
                "Failed to create table with exception: "
        );
    }

    /**
     * Drop a database table.
     * @param tableName name of table to drop
     * @return 0 if successful, -1 otherwise
     */
    public int drop_table(String tableName) throws IllegalArgumentException {
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid null argument for tableName.");
        }

        return updateDatabaseInternal(
                String.format("DROP TABLE %s;", tableName),
                "Failed to drop table with exception: "
        );
    }

    /**
     * Inserts an entry into the specified table.
     * @param tableName table to insert into
     * @param columns list of column names
     * @param values list of entry values
     * @return row count or -1 if error occurs
     */
    public int insert(String tableName, String[] columns, String[] values) throws IllegalArgumentException {
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid null argument for tableName.");
        } else if (columns == null) {
            throw new IllegalArgumentException("Invalid null argument for columns.");
        } else if (values == null) {
            throw new IllegalArgumentException("Invalid null argument for values.");
        } else if (columns.length != values.length) {
            throw new IllegalArgumentException("Column and value array lengths do not match.");
        }
        StringBuilder colStrBuilder = new StringBuilder();
        StringBuilder valueStrBuilder = new StringBuilder();

        // build column and value strings
        colStrBuilder.append("(");
        valueStrBuilder.append("VALUES (");
        for (int i = 0; i < columns.length; i++) {
            colStrBuilder.append(String.format("%s", columns[i]));
            valueStrBuilder.append(String.format("'%s'", values[i]));

            // handle last case
            if (i == columns.length - 1) {
                colStrBuilder.append(")");
                valueStrBuilder.append(")");
            } else {
                colStrBuilder.append(",");
                valueStrBuilder.append(",");
            }
        }

        return updateDatabaseInternal(
                String.format("INSERT INTO %s %s %s;", tableName, colStrBuilder.toString(), valueStrBuilder.toString()),
                        "Failed to insert into table with exception: "
        );
    }

    /**
     * Updates an entries in the specified table.
     * @param tableName table to update
     * @param columns list of columns to update
     * @param values list of entry values
     * @return row count or -1 if error occurs
     */
    public int update(String tableName, String[] columns, String[] values) throws IllegalArgumentException {
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid null argument for tableName.");
        } else if (columns == null) {
            throw new IllegalArgumentException("Invalid null argument for columns.");
        } else if (values == null) {
            throw new IllegalArgumentException("Invalid null argument for values.");
        } else if (columns.length != values.length) {
            throw new IllegalArgumentException("Column and value array lengths do not match.");
        }

        StringBuilder valueStrBuilder = new StringBuilder();

        // build value strings
        for (int i = 0; i < columns.length; i++) {
            valueStrBuilder.append(columns[i]);
            valueStrBuilder.append(" = ");
            valueStrBuilder.append(String.format("'%s'", values[i]));

            // handle last case
            if (i != columns.length - 1) {
                valueStrBuilder.append(",");
            }
        }

        return updateDatabaseInternal(
                String.format("UPDATE %s SET %s;", tableName, valueStrBuilder.toString()),
                "Failed to update table with exception: "
        );
    }

    /**
     * Updates an entries in the specified table with a given condition.
     * @param tableName table to update
     * @param columns list of columns to update
     * @param values list of entry values
     * @param condition condition to select which entries to update
     * @return row count or -1 if error occurs
     */
    public int update(String tableName, String[] columns, String[] values, String condition) throws IllegalArgumentException {
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid null argument for tableName.");
        } else if (columns == null) {
            throw new IllegalArgumentException("Invalid null argument for columns.");
        } else if (values == null) {
            throw new IllegalArgumentException("Invalid null argument for values.");
        } else if (condition == null) {
            throw new IllegalArgumentException("Invalid null argument for condition.");
        } else if (columns.length != values.length) {
            throw new IllegalArgumentException("Column and value array lengths do not match.");
        }

        StringBuilder valueStrBuilder = new StringBuilder();

        // build value strings
        for (int i = 0; i < columns.length; i++) {
            valueStrBuilder.append(columns[i]);
            valueStrBuilder.append(" = ");
            valueStrBuilder.append(String.format("'%s'", values[i]));

            // handle last case
            if (i != columns.length - 1) {
                valueStrBuilder.append(",");
            }
        }

        return updateDatabaseInternal(
                String.format("UPDATE %s SET %s WHERE %s;", tableName, valueStrBuilder.toString(), condition),
                "Failed to update table with exception: "
        );
    }

    /**
     * Deletes entries in the specified table.
     * @param tableName table to delete entries from
     * @param condition condition to select which entries to delete
     * @return row count or -1 if error occurs
     */
    public int delete(String tableName, String condition) throws IllegalArgumentException {
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid null argument for tableName.");
        } else if (condition == null) {
            throw new IllegalArgumentException("Invalid null argument for condition.");
        }

        return updateDatabaseInternal(
                String.format("DELETE FROM %s WHERE %s;", tableName, condition),
                "Failed to delete from table with exception: "
        );
    }

    /**
     * Queries entries from a table
     * @param tableName table to query from
     * @param columns columns to select
     * @return query data or null if error occurs
     */
    public Map[] select(String tableName, String[] columns) throws IllegalArgumentException {
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid null argument for tableName.");
        } else if (columns == null) {
            throw new IllegalArgumentException("Invalid null argument for columns.");
        }

        StringBuilder colStrBuilder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            colStrBuilder.append(String.format("%s", columns[i]));

            // handle last case
            if (i != columns.length - 1) {
                colStrBuilder.append(",");
            }
        }

        return queryDatabaseInternal(
                String.format("SELECT %s FROM %s;", colStrBuilder.toString(), tableName),
                columns,
                "Failed to query with exception: "
        );
    }

    /**
     * Queries entries from a table with a given condition
     * @param tableName table to query from
     * @param columns columns to select
     * @param condition condition to specify which entries to query
     * @return query data or null if error occurs
     */
    public Map[] select(String tableName, String[] columns, String condition) {
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid null argument for tableName.");
        } else if (columns == null) {
            throw new IllegalArgumentException("Invalid null argument for columns.");
        } else if (condition == null) {
            throw new IllegalArgumentException("Invalid null argument for condition.");
        }

        StringBuilder colStrBuilder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            colStrBuilder.append(String.format("%s", columns[i]));

            // handle last case
            if (i != columns.length - 1) {
                colStrBuilder.append(",");
            }
        }

        return queryDatabaseInternal(
                String.format("SELECT %s FROM %s WHERE %s;", colStrBuilder.toString(), tableName, condition),
                columns,
                "Failed to query with exception: "
        );
    }

    /**
     * Internal function to update the database
     * @param sql SQL statement to execute
     * @param errorMessage error message if execution fails
     * @return row count or -1 if error occurs
     */
    private int updateDatabaseInternal(String sql, String errorMessage) {
        try {
            Statement stmt = conn.createStatement();
            int res = stmt.executeUpdate(sql);
            stmt.close();
            return res;
        } catch (SQLException sqle) {
            logger.log(Level.SEVERE, errorMessage + sqle);
            return -1;
        }
    }

    /**
     * Internal function to query the database
     * @param sql SQL statement to execute
     * @param columns columns to query
     * @param errorMessage error message if execution fails
     * @return query data or null if error occurs
     */
    private Map[] queryDatabaseInternal(String sql, String[] columns, String errorMessage) {
        Map[] query;
        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet res = stmt.executeQuery(sql);

            // get the number of entries and initialize query
            res.last();
            int entry_count = res.getRow();
            boolean valid = res.first();
            query = new Map[entry_count];

            // iterate through every row
            int row = 0;
            do {
                if (valid) { // check no-entries edge case
                    // retrieve column-value map
                    Map<String, Object> entry = new HashMap<>();
                    for (String col : columns) {
                        entry.put(col, res.getObject(col));
                    }
                    query[row] = entry;
                    row++;
                }
            } while (res.next());
            stmt.close();
        } catch (SQLException sqle) {
            logger.log(Level.SEVERE, errorMessage + sqle);
            return null;
        }

        return query;
    }

    /**
     * Getter for url.
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Getter for user.
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * Getter for pw.
     * @return pw
     */
    public String getPw() {
        return pw;
    }

    /**
     * Setter for url.
     * @param url new url
     */
    public void setUrl(String url) throws IllegalArgumentException {
        if (url == null) {
            throw new IllegalArgumentException("Invalid null argument for url.");
        }
        this.url = url;
    }

    /**
     * Setter for user.
     * @param user new user
     */
    public void setUser(String user) throws IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException("Invalid null argument for user.");
        }
        this.user = user;
    }

    /**
     * Setter for pw
     * @param pw new pw
     */
    public void setPw(String pw) throws IllegalArgumentException {
        if (pw == null) {
            throw new IllegalArgumentException("Invalid null argument for pw.");
        }
        this.pw = pw;
    }
}
