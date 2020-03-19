import DataUtilities.DatabaseConnector;
import DataUtilities.TextLoader;

import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DataUtilitiesTests {
    private static DatabaseConnector dbConn;
    @BeforeAll
    static void initializeDBConnector() {
        dbConn = new DatabaseConnector(
                "jdbc:postgresql://localhost:5432/password_plus_test",
                "postgres",
                "123");
        dbConn.connect();

        assertEquals("jdbc:postgresql://localhost:5432/password_plus_test", dbConn.getUrl());
        assertEquals("postgres", dbConn.getUser());
        assertEquals("123", dbConn.getPw());
    }

    @AfterAll
    static void disconnectDB() {
        // drop all tables
        dbConn.drop_table("test");
        dbConn.drop_table("test2");

        // disconnect
        dbConn.disconnect();
    }

    @Test
    void readTextTest() {
        Object[] words = TextLoader.readText("./data/test.txt");

        // check first 3 words
        assertEquals("the", words[0]);
        assertEquals("of", words[1]);
        assertEquals("and", words[2]);

        // check length of words array
        assertEquals(10000, words.length);

        // check null argument throws exception
        assertThrows(
                IllegalArgumentException.class,
                () -> TextLoader.readText(null)
        );
    }

    @Test
    void createTableTest() {
        // create test table
        int val = dbConn.create_table(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"varchar(255)", "varchar(255)"}
        );
        assertEquals(0, val);

        // create table with same name and check error
        val = dbConn.create_table(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"varchar(255)", "varchar(255)"}
        );
        assertEquals(-1, val);

        // check null argument throws exception
        assertThrows(
                IllegalArgumentException.class,
                () -> dbConn.create_table(null, null, null)
        );

        // check different column/datatypes lengths throws exception
        assertThrows(
                IllegalArgumentException.class,
                () -> dbConn.create_table("test", new String[]{"error"}, new String[]{"error", "error"})
        );

        // drop test table
        dbConn.drop_table("test");
    }

    @Test
    void dropTableTest() {
        // create test table
        dbConn.create_table(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"varchar(255)", "varchar(255)"}
        );

        // drop test table
        int val = dbConn.drop_table("test");
        assertEquals(0, val);

        // try dropping a table that does not exists and check error
        val = dbConn.drop_table("test2");
        assertEquals(-1, val);

        // check null argument throws exception
        assertThrows(
                IllegalArgumentException.class,
                () -> dbConn.drop_table(null)
        );
    }

    @Test
    void insertTest() {
        // create test table
        dbConn.create_table(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"varchar(255)", "varchar(255)"}
        );

        // insert an entry and check that one entry was inserted
        int val = dbConn.insert(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"test_val1", "test_val2"}
        );
        assertEquals(1, val);

        // query the entry, check values, and query length
        Map[] query = dbConn.select("test", new String[]{"var1", "var2"});
        assertEquals("test_val1", query[0].get("var1"));
        assertEquals("test_val2", query[0].get("var2"));
        assertEquals(1, query.length);

        // insert an entry with invalid column and check error
        val = dbConn.insert(
                "test",
                new String[]{"var1", "var3"},
                new String[]{"test_val1", "test_val2"}
        );
        assertEquals(-1, val);

        // check null argument throws exception
        assertThrows(
                IllegalArgumentException.class,
                () -> dbConn.insert(null, null, null)
        );

        // check different column/values lengths throws exception
        assertThrows(
                IllegalArgumentException.class,
                () -> dbConn.insert("test", new String[]{"error"}, new String[]{"error", "error"})
        );

        // drop test table
        dbConn.drop_table("test");
    }

    @Test
    void deleteTest() {
        // create test table
        dbConn.create_table(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"varchar(255)", "varchar(255)"}
        );

        // insert 2 entries
        dbConn.insert(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"test_val1", "test_val2"}
        );
        dbConn.insert(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"test_val1", "test_val3"}
        );

        // delete the two values
        int val = dbConn.delete("test", "var1 = 'test_val1'");
        assertEquals(2, val);

        // query the table and check that length is 0
        Map[] query = dbConn.select("test", new String[]{"var1", "var2"});
        assertEquals(0, query.length);

        // delete again and check no errors
        val = dbConn.delete("test", "var1 = 'test_val1'");
        assertEquals(0, val);

        // delete with invalid conditions and check error
        val = dbConn.delete("test", "var3 = 'test_val1'");
        assertEquals(-1, val);

        // check null argument throws exception
        assertThrows(
                IllegalArgumentException.class,
                () -> dbConn.delete(null, null)
        );

        // drop test table
        dbConn.drop_table("test");
    }

    @Test
    void updateTest() {
        // create test table
        dbConn.create_table(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"varchar(255)", "varchar(255)"}
        );

        // insert 3 entries
        dbConn.insert(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"test_val1", "test_val2"}
        );
        dbConn.insert(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"test_val1", "test_val3"}
        );
        dbConn.insert(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"test_val1", "test_val3"}
        );

        // update the entry and check that 2 rows were changed
        int res = dbConn.update(
                "test",
                new String[]{"var2"},
                new String[]{"test_val4"},
                "var2 = 'test_val3'"
        );
        assertEquals(2, res);

        // query the database
        Map[] query = dbConn.select("test", new String[]{"var1", "var2"});

        // check only entry 2 var 2 got updated
        assertEquals("test_val4", query[1].get("var2"));
        assertEquals("test_val1", query[1].get("var1"));

        // check only entry 3 var 2 got updated
        assertEquals("test_val4", query[2].get("var2"));
        assertEquals("test_val1", query[2].get("var1"));

        // check that entry 1 did not get updated
        assertEquals("test_val2", query[0].get("var2"));
        assertEquals("test_val1", query[0].get("var1"));

        // update that update without condition updates all 3
        res = dbConn.update(
                "test",
                new String[]{"var1"},
                new String[]{"test_val5"}
        );
        assertEquals(3, res);

        // update with invalid conditions and check error
        res = dbConn.update(
                "test",
                new String[]{"var3"},
                new String[]{"test_val4"},
                "var2 = 'test_val3'"
        );
        assertEquals(-1, res);

        // check null argument throws exception
        assertThrows(
                IllegalArgumentException.class,
                () -> dbConn.update(null, null, null, null)
        );

        // check different column/values lengths throws exception
        assertThrows(
                IllegalArgumentException.class,
                () -> dbConn.update("test", new String[]{"error"}, new String[]{"error", "error"})
        );

        // drop test table
        dbConn.drop_table("test");
    }

    @Test
    void selectTest() {
        // create test table
        dbConn.create_table(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"varchar(255)", "varchar(255)"}
        );

        // insert 3 entries
        dbConn.insert(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"test_val1", "test_val2"}
        );
        dbConn.insert(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"test_val1", "test_val3"}
        );
        dbConn.insert(
                "test",
                new String[]{"var1", "var2"},
                new String[]{"test_val1", "test_val3"}
        );

        // query the database and check length
        Map[] query = dbConn.select("test", new String[]{"var1", "var2"});
        assertEquals(3, query.length);

        // check values
        assertEquals("test_val1", query[0].get("var1"));
        assertEquals("test_val2", query[0].get("var2"));
        assertEquals("test_val1", query[1].get("var1"));
        assertEquals("test_val3", query[1].get("var2"));
        assertEquals("test_val1", query[2].get("var1"));
        assertEquals("test_val3", query[2].get("var2"));

        // query the database with invalid conditions and check error
        query = dbConn.select("test", new String[]{"var1", "var2"}, "var3 = 'test_val3'");
        assertNull(query);

        // check null argument throws exception
        assertThrows(
                IllegalArgumentException.class,
                () -> dbConn.select(null, null, null)
        );

        // drop test table
        dbConn.drop_table("test");
    }
}
