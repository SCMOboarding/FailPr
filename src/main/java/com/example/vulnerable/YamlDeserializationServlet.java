package com.example.vulnerable;

import org.yaml.snakeyaml.Yaml;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * VULNERABILITY: Unsafe YAML Deserialization (CWE-502) - CRITICAL
 * SnakeYAML's default Yaml() constructor allows arbitrary Java object instantiation.
 */
public class YamlDeserializationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(req.getInputStream()));
        StringBuilder yamlInput = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            yamlInput.append(line).append("\n");
        }

        // VULNERABILITY: Unsafe YAML deserialization - default Yaml() allows type coercion
        // Attacker payload: !!javax.script.ScriptEngineManager [!!java.net.URLClassLoader [...]]
        Yaml yaml = new Yaml();  // CRITICAL: should use SafeConstructor
        Object result = yaml.load(yamlInput.toString());  // RCE risk

        resp.getWriter().println("Loaded: " + result);
    }
}
