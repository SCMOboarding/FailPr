package com.example.vulnerable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * VULNERABILITY: Server-Side Request Forgery (CWE-918) - HIGH
 * User-supplied URL is fetched by the server without validation,
 * allowing internal network scanning, metadata service access, etc.
 */
public class SsrfServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String targetUrl = req.getParameter("url");  // attacker-controlled
        PrintWriter out = resp.getWriter();

        // VULNERABILITY: SSRF - no URL validation or allowlist (CWE-918)
        // Attacker can supply: http://169.254.169.254/latest/meta-data/ (AWS metadata)
        //                      http://internal-service/admin
        //                      file:///etc/passwd
        URL url = new URL(targetUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  // CRITICAL
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            // VULNERABILITY: XSS - response reflected without encoding (CWE-79)
            out.println(line);
        }
    }
}
