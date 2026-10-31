package com.example.vulnerable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * VULNERABILITY: Open Redirect (CWE-601) - HIGH
 * Unvalidated redirect destination from user input.
 */
public class OpenRedirectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String redirectUrl = req.getParameter("redirect");

        // VULNERABILITY: Open Redirect - no URL validation (CWE-601)
        // Attacker can redirect users to: https://evil.com/phishing
        resp.sendRedirect(redirectUrl);  // HIGH: open redirect
    }
}
