import org.h2.tools.Server;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS todos (id IDENTITY, text VARCHAR, is_done BOOLEAN, user_id INT);");
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
    }

    public static void insertTodo(Connection conn, String text, int user_id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO todos VALUES (NULL, ?, FALSE, ?);");
        stmt.setString(1, text);
        stmt.setInt(2, user_id);
        stmt.execute();
    }

    public static ToDoItem selectTodo(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM todos INNER JOIN users ON todos.user_id = users.id WHERE todos.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int user_id = results.getInt("todos.user_id");
            String text = results.getString("todos.text");
            boolean isDone = results.getBoolean("todos.is_done");
            return new ToDoItem(id, text, isDone, user_id);
        }
        return null;
    }

    public static ArrayList<ToDoItem> selectToDos(Connection conn, Integer userId) throws SQLException {
        ArrayList<ToDoItem> items = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos JOIN users ON todos.user_id = users.id WHERE users.id=?;");
        stmt.setInt(1, userId);
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            String text = results.getString("text");
            boolean isDone = results.getBoolean("is_done");
//            int user_id = results.getInt("users.id");
            int id = results.getInt("todos.id");
            items.add(new ToDoItem(id, text, isDone, userId));
        }
        return items;
    }

    public static void updateToDo(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE todos SET is_done = NOT is_done WHERE todos.id = ?;");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void deleteTodo(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM todos WHERE todos.id = ?;");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?);");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name=?;");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if(results.next()){
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, name, password);
        }
        return null;
    }

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a name");
        String userName = scanner.nextLine();
        System.out.println("Enter password");
        String password = scanner.nextLine();
        User currentUser = selectUser(conn, userName);
        if(currentUser == null) {
            insertUser(conn, userName, password);
        }
        while (true) {
            System.out.println("1. Create to-do item");
            System.out.println("2. Toggle to-do item");
            System.out.println("3. List to-do items");
            System.out.println("4. Delete item");
            String option = scanner.nextLine();
            if (option.equals("1")) {
                System.out.println("Create to-do item");
                String text = scanner.nextLine();
                //get id for user from user table that matches name for user_id
                insertTodo(conn, text, currentUser.id);
            } else if (option.equals("2")) {
                System.out.println("Toggle item.");
                int itemNum = Integer.parseInt(scanner.nextLine());
                updateToDo(conn, itemNum);
            } else if (option.equals("3")) {
                ArrayList<ToDoItem> items = selectToDos(conn, currentUser.id);
                for (ToDoItem item : items) {
                    String checkbox = "[ ]";
                    if (item.isDone) {
                        checkbox = "[X] ";
                    }
                    System.out.printf("%s  %d. %s\n", checkbox, item.id, item.text);
                }
            } else if(option.equals("4")){
                System.out.println("Delete item");
                int deleteNum = Integer.parseInt(scanner.nextLine());
                deleteTodo(conn, deleteNum);
            }
        }
    }
}