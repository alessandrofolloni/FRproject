package FR.jdbc;

import FR.utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A class for testing basic operations with JDBC It supports both MySQL and
 * SQLite
 *
 * @author Nicola Bicocchi
 */
public class BasicOperations {
    Statement statement;

    public BasicOperations() throws SQLException {

        /*DBManager.setConnection(
                Utils.JDBC_Driver_SQLite,
                Utils.JDBC_URL_SQLite);
        statement = DBManager.getConnection().createStatement();
*/

        DBManager.setConnection(
                Utils.JDBC_Driver_MySQL,
                Utils.JDBC_URL_MySQL);
        statement = DBManager.getConnection().createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);


        try {
            /*
             * Simple query for testing that everything is OK. If an exception raised, the
             * db is deleted and created from scratch.
             */
            statement.executeQuery("SELECT nome FROM persona ");
        } catch (SQLException e) {
           /* statement.executeUpdate("DROP TABLE IF EXISTS book");
            statement.executeUpdate("CREATE TABLE book (" + "id INTEGER PRIMARY KEY, " + "title VARCHAR(30), "
                    + "author VARCHAR(30), " + "pages INTEGER)");

            statement.executeUpdate(
                    "INSERT INTO book (id, title, author, pages) VALUES(1, 'The Lord of the Rings', 'Tolkien', 241)");
            statement.executeUpdate("INSERT INTO book (id, title, author, pages) VALUES(2, 'Fight Club', 'Palahniuk', 212)");
            statement.executeUpdate(
                    "INSERT INTO book (id, title, author, pages) VALUES(3, 'Computer Networks', 'Tanenbaum', 313)");
            statement.executeUpdate(
                    "INSERT INTO book (id, title, author, pages) VALUES(4, 'Affective Computing', 'Picard', 127)");*/
            System.out.println("errore");
        }
    }

    public static void main(String[] args) {
        try {
            new BasicOperations().run();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {
            System.out.println("\n- inserting lines...");
            testInsert();
        } catch (SQLException a) {
            System.out.println("Something went wrong... " + a.getMessage());
        }
        try {
            System.out.println("\n- controllo insert...");
            testSelectAll("persona");
        } catch (SQLException a) {
            System.out.println("Something went wrong... " + a.getMessage());
        }
        try {
            System.out.println("\n- controllo insert...");
            testDelete("cognome","artoni");
        } catch (SQLException a) {
            System.out.println("Something went wrong... " + a.getMessage());
        }
        try {
            System.out.println("\n- controllo finale...");
            testSelectAll("persona");
        } catch (SQLException a) {
            System.out.println("Something went wrong... " + a.getMessage());
        }



        /*
        try {
            System.out.println("\n- updating database...");
            testUpdate();
        } catch (SQLException e) {
            System.out.println("Something went wrong... " + e.getMessage());
        }

        try {
            System.out.println("\n- reading database...");
            testSelect();
        } catch (SQLException e) {
            System.out.println("Something went wrong... " + e.getMessage());
        }

        try {
            System.out.println("\n- test scrollable...");
            testScrollable();
        } catch (SQLException e) {
            System.out.println("Something went wrong... " + e.getMessage());
        }

        try {
            System.out.println("\n- test updateable...");
            testUpdateable();
        } catch (SQLException e) {
            System.out.println("Something went wrong... " + e.getMessage());
        }

        try {
            System.out.println("\n- reading database...");
            testSelect();
        } catch (SQLException e) {
            System.out.println("Something went wrong... " + e.getMessage());
        }

        try {
            System.out.println("\n- test sensitive...");
            testSensitive();
        } catch (SQLException e) {
            System.out.println("Something went wrong... " + e.getMessage());
        }
        */
        try {
            System.out.println("\n- closing database...");
            DBManager.close();
        } catch (SQLException e) {
            System.out.println("Something went wrong... " + e.getMessage());
        }
    }

    /**
     * Reads the content of the person table Results are limited using "LIMIT
     * 100" This is useful for very large tables
     */
    public void testSelectAll(String s) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM "+s+";  ");
        while (rs.next()) {
            printRow(rs);
        }
    }
    public void testInsert() throws SQLException {
        System.out.print("sto eseguendo: insert into persona (nome,cognome,ruolo) values (\"zio\",\"fabrizio\", \"difensore\"); ");
        statement.executeUpdate(
                "insert into persona (nome,cognome,ruolo) values (\"zio\",\"fabrizio\", \"difensore\"); ");

    }
    public void testDelete(String nomecol ,String val) throws SQLException {
        System.out.print("sto eseguendo: DELETE FROM persona WHERE "+  nomecol+ "='"+val+"'; ");
        statement.executeUpdate(
                "DELETE FROM persona WHERE "+  nomecol+ "='"+val+"'; ");

    }
    /**
     * Update the content of the book table
     */
    public void testUpdate() throws SQLException {
        statement.executeUpdate(
                "UPDATE book SET title='Il Principe', " + "author='Macchiavelli', " + "pages=176 " + "WHERE id=1");
    }

    /**
     * Test Scrollable ResultSet
     */
    public void testScrollable() throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM book LIMIT 100 OFFSET 0");
        // Third record
        rs.absolute(3);
        printRow(rs);

        // Previous record
        rs.previous();
        printRow(rs);

        // +2 records from current position
        rs.relative(2);
        printRow(rs);
    }

    /**
     * Test Updateable ResultSet Increment pages of one element
     */
    public void testUpdateable() throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM book LIMIT 100 OFFSET 0");
        while (rs.next()) {
            int pages = rs.getInt("pages");
            rs.updateInt("pages", pages + 1);
            rs.updateRow();
        }
    }

    /**
     * Test Sensitive ResultSet
     */
    public void testSensitive() throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM book LIMIT 100 OFFSET 0");
        for (int retry = 0; retry < 10; retry++) {
            while (rs.next()) {
                rs.refreshRow();
                printRow(rs);
            }
            System.out.printf("\n[%d] awaiting for external changes 6s...", retry);
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                // do nothing
            }
            rs.beforeFirst();
        }
    }

    /**
     * Prints the current ResultSet row
     */
    public void printRow(ResultSet rs) throws SQLException {
        System.out.println("nome=" + rs.getString("nome") + ", cognome=" + rs.getString("cognome") + ", ruolo="
                + rs.getString("ruolo") );

    }
    public void printRowNome(ResultSet rs) throws SQLException {
        System.out.println("nome=" + rs.getString("nome") );

    }
}
