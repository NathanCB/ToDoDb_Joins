import org.junit.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class MainTest {

    public MainTest() throws SQLException {
    }

    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }


    @Test
    public void testUser()throws SQLException{
        Connection conn = startConnection();
        Main.insertUser(conn, "Zoe", "March");
        User user = Main.selectUser(conn, "Zoe");
        conn.close();
        assertTrue(user != null);
        assertTrue(user.password.equals("March"));
    }

    @Test
    public void testSelectTodo() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Zoe", "March");          // create a user
        Main.insertTodo(conn, "New Test Todo",1);          // create a Todo for that user
        ToDoItem toDoItem = Main.selectTodo(conn, 1);
        conn.close();
        assertTrue(toDoItem != null);
    }

    @Test
    public void testUpdateTodo() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Zoe", "March");
        ToDoItem selectTodoItem = Main.selectTodo(conn, 1);
        Main.updateToDo(conn, 1);
        conn.close();
    }

    @Test
    public void testDeleteTodo() throws SQLException{
        Connection conn = startConnection();
        Main.insertUser(conn, "Zoe", "March");
        ToDoItem deleteTodoItem = Main.selectTodo(conn, 1);
        Main.deleteTodo(conn, 1);
        conn.close();
    }
}