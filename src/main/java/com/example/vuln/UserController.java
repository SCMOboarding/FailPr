package com.example.vuln;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

// Intentionally vulnerable test fixture for SAST scan validation. Do not use in production.
public class UserController {

    // Hardcoded credential
    private static final String DB_PASSWORD = "P@ssw0rd123!";
    private static final String API_KEY = "AKIAIOSFODNN7EXAMPLE";

    // SQL injection: user input concatenated into query
    public ResultSet findUser(String username) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", DB_PASSWORD);
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM users WHERE name = '" + username + "'";
        return stmt.executeQuery(query);
    }

    // OS command injection: user input passed to shell
    public String ping(String host) throws Exception {
        Process p = Runtime.getRuntime().exec("sh -c ping -c 1 " + host);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return reader.readLine();
    }
}
