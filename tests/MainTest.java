import org.junit.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class MainTest {

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


}