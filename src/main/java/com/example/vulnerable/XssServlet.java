package com.example.vulnerable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * VULNERABILITY: Cross-Site Scripting (CWE-79) - HIGH
 * Reflected and stored XSS via unsanitized user input in HTML output.
 * Also demonstrates insecure cookie configuration.
 */
public class XssServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String name  = req.getParameter("name");
        String search = req.getParameter("q");
        PrintWriter out = resp.getWriter();

        resp.setContentType("text/html");

        // VULNERABILITY: Reflected XSS - no output encoding (CWE-79)
        out.println("<html><body>");
        out.println("<h1>Hello, " + name + "!</h1>");            // CRITICAL: XSS
        out.println("<p>Search results for: " + search + "</p>"); // CRITICAL: XSS

        // VULNERABILITY: DOM-based XSS via document.write with user data
        out.println("<script>");
        out.println("  document.write('Welcome back " + name + "');"); // XSS in JS context
        out.println("</script>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String sessionId = req.getParameter("session");

        // VULNERABILITY: Cookie missing HttpOnly and Secure flags (CWE-614, CWE-1004)
        Cookie sessionCookie = new Cookie("SESSIONID", sessionId);
        sessionCookie.setPath("/");
        // Missing: sessionCookie.setHttpOnly(true);
        // Missing: sessionCookie.setSecure(true);
        // Missing: sessionCookie.setMaxAge(...)
        resp.addCookie(sessionCookie);  // HIGH: cookie accessible via JS and plain HTTP

        // VULNERABILITY: Missing security headers
        // No X-Content-Type-Options, X-Frame-Options, CSP, etc.
        resp.getWriter().println("Session set.");
    }
}
