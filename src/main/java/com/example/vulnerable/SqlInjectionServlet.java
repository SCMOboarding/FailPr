package com.example.vulnerable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * VULNERABILITY: SQL Injection (CWE-89) - CRITICAL
 * User input is directly concatenated into SQL queries without sanitization.
 */
public class SqlInjectionServlet extends HttpServlet {

    // VULNERABILITY: Hardcoded credentials (CWE-798) - HIGH
    private static final String DB_URL      = "jdbc:h2:mem:testdb";
    private static final String DB_USER     = "admin";
    private static final String DB_PASSWORD = "admin123!";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String username = req.getParameter("username");
        PrintWriter out = resp.getWriter();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // VULNERABILITY: SQL Injection - raw string concatenation (CWE-89)
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);  // CRITICAL: unsanitized input

            while (rs.next()) {
                // VULNERABILITY: XSS - user-controlled data written to response (CWE-79)
                out.println("<p>User: " + rs.getString("username") + "</p>");
            }

        } catch (SQLException e) {
            // VULNERABILITY: Information Exposure - stack trace sent to client (CWE-209)
            e.printStackTrace(out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String id       = req.getParameter("id");
        String password = req.getParameter("password");

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // VULNERABILITY: SQL Injection in UPDATE (CWE-89)
            String update = "UPDATE users SET password='" + password
                    + "' WHERE id=" + id;
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(update);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
