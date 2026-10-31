package com.example.vulnerable;

import org.apache.log4j.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * VULNERABILITY: Log Injection / Log4Shell (CWE-117, CWE-20) - CRITICAL
 * Unsanitized user input logged via vulnerable Log4j 1.x.
 * Also demonstrates log forging.
 */
public class LogInjectionServlet extends HttpServlet {

    // VULNERABILITY: Using old Log4j 1.x (CVE-2019-17571) - CRITICAL
    private static final Logger logger = Logger.getLogger(LogInjectionServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String username   = req.getParameter("username");
        String userAgent  = req.getHeader("User-Agent");
        String xForwardedFor = req.getHeader("X-Forwarded-For");

        // VULNERABILITY: Log Injection - unsanitized input in log messages (CWE-117)
        // Attacker can inject newlines to forge log entries:
        //   username=admin\nINFO: User admin logged in successfully
        logger.info("Login attempt for user: " + username);         // HIGH
        logger.warn("Request from: " + xForwardedFor);              // HIGH
        logger.debug("User-Agent: " + userAgent);                   // HIGH

        // VULNERABILITY: Sensitive data in logs (CWE-532)
        String password = req.getParameter("password");
        logger.info("Auth attempt: user=" + username + " pass=" + password);  // CRITICAL

        resp.getWriter().println("Logged.");
    }
}
