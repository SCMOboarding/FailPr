package com.example.vulnerable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Base64;

/**
 * VULNERABILITY: Insecure Deserialization (CWE-502) - CRITICAL
 * Deserializes untrusted, user-supplied data without validation.
 * Combined with commons-collections 3.2.1 gadget chain = Remote Code Execution.
 */
public class DeserializationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String serializedData = req.getParameter("data");
        PrintWriter out = resp.getWriter();

        try {
            // VULNERABILITY: Insecure Deserialization - no type validation (CWE-502)
            byte[] bytes = Base64.getDecoder().decode(serializedData);
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(bytes));  // CRITICAL: RCE risk
            Object obj = ois.readObject();
            out.println("Deserialized: " + obj.toString());

        } catch (ClassNotFoundException e) {
            out.println("Class not found: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // VULNERABILITY: Deserializing directly from request stream (CWE-502)
        try (ObjectInputStream ois = new ObjectInputStream(req.getInputStream())) {
            Object obj = ois.readObject();  // CRITICAL
            resp.getWriter().println(obj);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
