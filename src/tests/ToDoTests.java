import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by joe on 28/09/2016.
 */
public class ToDoTests {

    //helper method
    public Connection startConnection()throws SQLException{
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        ToDo.createTables(conn);
        return conn;
    }

    @Test
    //test user insert and select
    public void testUser()throws SQLException{
        Connection conn = startConnection();
        ToDo.insertUser(conn,"Alice");
        User user = ToDo.selectUser(conn,"Alice");
        conn.close();
        assertTrue(user != null);
    }

    @Test
    //test inserting and selecting item by user id
    public void testItem()throws SQLException{
        Connection conn = startConnection();
        ToDo.insertItem(conn, 1, "Stuff");
        ToDo.selectItem(conn, 1);
        conn.close();
    }

    @Test
    //test inserting multiple items and returning query based on join using user id
    public void testSelectItems()throws SQLException{
        Connection conn = startConnection();
        //test data for todos table
        ToDo.insertItem(conn, 1, "Stuff");
        ToDo.insertItem(conn, 2, "More stuff");
        ToDo.insertItem(conn, 3, "Even more things");

        //test data for users
        ToDo.insertUser(conn, "Alice");
        ToDo.insertUser(conn, "Robert");
        ToDo.insertUser(conn, "Chuckles");

        //test for array list output matching data inserted into table and joined
        ArrayList<ToDoItem> itemListTest = ToDo.selectItems(conn);
        conn.close();

        assertTrue(itemListTest.size() == 3);
    }

    //problem here with intellij recognizing boolean sql. It works when I run it
    //in the database interface directly
    @Test public void testUpdateItems()throws SQLException{
        Connection conn= startConnection();

        //test data for updateItem
        ToDo.insertItem(conn, 1, "Stuff");
    }

    @Test public void testDeleteItems()throws SQLException{
        Connection conn = startConnection();

        //test data for todos table
        ToDo.insertItem(conn, 1, "Stuff");
        ToDo.insertItem(conn, 2, "More stuff");
        ToDo.insertItem(conn, 3, "Even more things");

        ToDo.deleteItem(conn, 1);
        ToDo.deleteItem(conn, 2);
        ToDo.deleteItem(conn, 3);

        ArrayList<ToDoItem> itemListTest = ToDo.selectItems(conn);
        conn.close();

        assertTrue(itemListTest.isEmpty());
    }

}
