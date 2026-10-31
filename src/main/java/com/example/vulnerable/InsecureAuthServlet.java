package com.example.vulnerable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Base64;

/**
 * VULNERABILITIES: Authentication and Session Management Issues
 *   - CWE-287: Improper Authentication - HIGH
 *   - CWE-384: Session Fixation - HIGH
 *   - CWE-521: Weak Password Requirements - MEDIUM
 *   - CWE-256: Plaintext Storage of Password - HIGH
 */
public class InsecureAuthServlet extends HttpServlet {

    // VULNERABILITY: Hardcoded admin credentials (CWE-798) - CRITICAL
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin";

    private static final String DB_URL  = "jdbc:h2:mem:authdb";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        PrintWriter out = resp.getWriter();

        // VULNERABILITY: Plaintext password comparison via Base64 (not encryption) (CWE-256)
        String encodedPass = Base64.getEncoder().encodeToString(password.getBytes());

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // VULNERABILITY: SQL Injection + plaintext password storage (CWE-89, CWE-256)
            String query = "SELECT * FROM users WHERE username='" + username
                    + "' AND password='" + encodedPass + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                HttpSession session = req.getSession(true);

                // VULNERABILITY: Session Fixation - session not invalidated before login (CWE-384)
                // Should call: req.getSession(false).invalidate(); first

                // VULNERABILITY: Sensitive data in session without encryption
                session.setAttribute("username", username);
                session.setAttribute("role", rs.getString("role"));
                session.setAttribute("password", password);  // storing password in session!

                out.println("Login successful");
            } else {
                // VULNERABILITY: Username enumeration via different error messages
                out.println("Invalid password for user: " + username);  // reveals valid usernames
            }

        } catch (SQLException e) {
            // VULNERABILITY: Stack trace information disclosure (CWE-209)
            e.printStackTrace(out);
        }
    }

    // VULNERABILITY: Broken access control - no authorization check (CWE-862)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String action = req.getParameter("action");
        PrintWriter out = resp.getWriter();

        // Missing authentication/authorization check before privileged operations
        if ("deleteUser".equals(action)) {
            String userId = req.getParameter("id");
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                // VULNERABILITY: SQL Injection (CWE-89)
                conn.createStatement().execute("DELETE FROM users WHERE id=" + userId);
                out.println("User deleted: " + userId);
            } catch (SQLException e) {
                e.printStackTrace(out);
            }
        }
    }
}
