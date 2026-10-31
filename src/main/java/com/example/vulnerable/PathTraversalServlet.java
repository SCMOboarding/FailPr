package com.example.vulnerable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * VULNERABILITY: Path Traversal (CWE-22) - HIGH
 * Unsanitized file path from user input allows reading arbitrary files.
 */
public class PathTraversalServlet extends HttpServlet {

    private static final String BASE_DIR = "/var/app/files/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String filename = req.getParameter("name");
        PrintWriter out = resp.getWriter();

        // VULNERABILITY: Path Traversal - no path normalization (CWE-22)
        // Attacker can use: ../../etc/passwd
        File file = new File(BASE_DIR + filename);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            out.println(line);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String path    = req.getParameter("path");
        String content = req.getParameter("content");

        // VULNERABILITY: Arbitrary file write via path traversal (CWE-22)
        Files.write(Paths.get(BASE_DIR + path), content.getBytes());
    }
}
