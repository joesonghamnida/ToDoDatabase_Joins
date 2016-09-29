import org.h2.tools.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by joe on 28/09/2016.
 */

public class ToDo {

    public static void createTables(Connection conn)throws SQLException{
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS todos (id IDENTITY, user_id INT, text VARCHAR, is_done BOOLEAN)");
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, user_name VARCHAR )");
    }

    public static void insertUser(Connection conn, String userName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES(NULL, ?)");
        stmt.setString(1, userName);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String userName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE user_name=?");
        stmt.setString(1, userName);
        ResultSet results = stmt.executeQuery();
        if (results.next())
        {
            String user_name = results.getString("user_name");
            return new User(user_name);
        }
        return null;
    }

    public static void insertItem(Connection conn, int user_id, String text) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO todos VALUES(NULL, ?, ?, FALSE)");
        stmt.setInt(1, user_id);
        stmt.setString(2, text);
        stmt.execute();
    }

    public static ToDoItem selectItem(Connection conn, int user_id)throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos INNER JOIN users ON todos.user_id=users.id WHERE todos.user_id=?");
        stmt.setInt(1,user_id);
        ResultSet results = stmt.executeQuery();
        if(results.next()){
            int id = results.getInt("id");
            int userId = results.getInt("user_id");
            String itemName= results.getString("text");
            boolean isDone = results.getBoolean("is_done");
            return new ToDoItem(id,userId, itemName, isDone);
        }
        return null;
    }

    public static ArrayList<ToDoItem> selectItems(Connection conn) throws SQLException {
        ArrayList<ToDoItem> items = new ArrayList<>();
        Statement stmt = conn.createStatement();

        ResultSet results = stmt.executeQuery("SELECT * FROM todos INNER JOIN users ON todos.user_id=users.id WHERE todos.user_id=users.id");

        while (results.next()) {
            int id = results.getInt("id");
            int user_id = results.getInt("user_id");
            String text = results.getString("text");
            boolean isDone = results.getBoolean("is_done");
            items.add(new ToDoItem(id, user_id, text, isDone));
        }
        return items;
    }

    public static void updateItem(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE todos set is_done = NOT is_done WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void deleteItem(Connection conn, int item_id)throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("DELETE from todos WHERE todos.id=?");
        stmt.setInt(1,item_id);
        stmt.execute();
    }

    public static void main(String[] args) throws SQLException {

        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");

        createTables(conn);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Create to-do item");
            System.out.println("2. Toggle to-do item");
            System.out.println("3. List to-do items");
            System.out.println("4. Delete item by it's ID");
            System.out.println("5. Exit program");

            String option = scanner.nextLine();

            if (option.equals("1")) {
                System.out.println("Enter the item to do");
                String item = scanner.nextLine();
                System.out.println("Enter your user ID");
                int userId = Integer.parseInt(scanner.nextLine());
                insertItem(conn, userId, item);


            } else if (option.equals("2")) {
                //2 is broken due to intellij not recognizing SQL boolean value for update
                //the sql code works if you run it directly in the h2 database interface
                System.out.println("Enter the number of the item you want to toggle:");
                int itemNum = Integer.valueOf(scanner.nextLine());
                updateItem(conn, itemNum);

            } else if (option.equals("3")) {
                ArrayList<ToDoItem> todos = selectItems(conn);
                for (ToDoItem item : todos) {
                    String checkbox = "[ ] ";
                    if (item.isDone) {
                        checkbox = "[x] ";
                    }
                    System.out.println(checkbox + item.id + ". " + item.text);
                }
            }
            else if (option.equals("4")){

                System.out.println("Please enter the item ID: ");
                int itemId = Integer.parseInt(scanner.nextLine());
                deleteItem(conn, itemId);
            }
            else if (option.equals("5")){
                System.out.println("You've completed all your tasks!");
                System.out.println("Your wife must be happy");
                System.exit(0);
            }

            else {
                System.out.println("Invalid option");
            }
        }
    }
}

