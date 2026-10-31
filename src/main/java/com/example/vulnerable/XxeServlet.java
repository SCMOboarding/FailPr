package com.example.vulnerable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import java.io.*;

/**
 * VULNERABILITY: XML External Entity Injection (CWE-611) - CRITICAL
 * XML parser configured to process external entities, enabling file disclosure and SSRF.
 */
public class XxeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        PrintWriter out = resp.getWriter();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // VULNERABILITY: XXE - external entities and DTDs are enabled (CWE-611)
            // Should set:
            //   factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            //   factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // But they are intentionally NOT set here.

            DocumentBuilder builder = factory.newDocumentBuilder();  // CRITICAL: default unsafe config
            Document doc = builder.parse(req.getInputStream());

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(out));

        } catch (ParserConfigurationException | SAXException | TransformerException e) {
            // VULNERABILITY: Information Exposure (CWE-209)
            out.println("Error: " + e.getMessage());
        }
    }
}
