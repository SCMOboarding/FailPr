package com.example.vulnerable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.*;

/**
 * VULNERABILITY: XPath Injection (CWE-643) - HIGH
 * User input embedded in XPath expression without sanitization.
 */
public class XpathInjectionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        PrintWriter out = resp.getWriter();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document doc = factory.newDocumentBuilder()
                    .parse(getClass().getResourceAsStream("/users.xml"));

            XPath xpath = XPathFactory.newInstance().newXPath();

            // VULNERABILITY: XPath Injection - unsanitized input (CWE-643)
            // Attacker payload: username=' or '1'='1
            String expression = "//user[username/text()='" + username
                    + "' and password/text()='" + password + "']";
            NodeList nodes = (NodeList) xpath.evaluate(
                    expression, doc, XPathConstants.NODESET);  // HIGH

            if (nodes.getLength() > 0) {
                out.println("Authenticated: " + username);
            } else {
                out.println("Authentication failed.");
            }

        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }
}
