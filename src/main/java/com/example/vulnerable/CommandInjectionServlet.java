package com.example.vulnerable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * VULNERABILITY: Command Injection (CWE-78) - CRITICAL
 * User-controlled input passed directly to OS command execution.
 */
public class CommandInjectionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String filename = req.getParameter("file");
        PrintWriter out = resp.getWriter();

        // VULNERABILITY: Command Injection - unsanitized input in exec (CWE-78)
        String[] cmd = {"/bin/sh", "-c", "cat " + filename};
        Process process = Runtime.getRuntime().exec(cmd);  // CRITICAL

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            // VULNERABILITY: XSS - output not encoded (CWE-79)
            out.println(line);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String host = req.getParameter("host");

        // VULNERABILITY: Command Injection via ProcessBuilder (CWE-78)
        ProcessBuilder pb = new ProcessBuilder("ping", "-c", "1", host);
        pb.redirectErrorStream(true);
        Process p = pb.start();  // CRITICAL: attacker controls 'host'

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
        PrintWriter out = resp.getWriter();
        String line;
        while ((line = reader.readLine()) != null) {
            out.println(line);
        }
    }
}
