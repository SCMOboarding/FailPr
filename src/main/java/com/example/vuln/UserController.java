package com.example.vuln;
import java.sql.*;
public class UserController {
    private static final String DB_PASSWORD = "P@ssw0rd123!";
    public ResultSet findUser(String username) throws Exception {
        Connection c = DriverManager.getConnection("jdbc:h2:mem:t", "sa", DB_PASSWORD);
        return c.createStatement().executeQuery("SELECT * FROM users WHERE name = '" + username + "'");
    }
}
