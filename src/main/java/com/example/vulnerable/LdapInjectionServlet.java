package com.example.vulnerable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

/**
 * VULNERABILITY: LDAP Injection (CWE-90) - HIGH
 * User input is embedded into LDAP filter without escaping.
 */
public class LdapInjectionServlet extends HttpServlet {

    // VULNERABILITY: Hardcoded LDAP credentials (CWE-798) - HIGH
    private static final String LDAP_URL      = "ldap://internal-ldap:389";
    private static final String LDAP_BIND_DN  = "cn=admin,dc=example,dc=com";
    private static final String LDAP_PASSWORD = "LdapAdmin#2024";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String username = req.getParameter("username");
        PrintWriter out = resp.getWriter();

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, LDAP_URL);
        env.put(Context.SECURITY_PRINCIPAL, LDAP_BIND_DN);
        env.put(Context.SECURITY_CREDENTIALS, LDAP_PASSWORD);

        try {
            DirContext ctx = new InitialDirContext(env);

            // VULNERABILITY: LDAP Injection - unsanitized input in filter (CWE-90)
            // Attacker can inject: *)(uid=*))(|(uid=*
            String filter = "(&(objectClass=user)(uid=" + username + "))";  // HIGH
            NamingEnumeration<?> results = ctx.search("dc=example,dc=com", filter,
                    new SearchControls());

            while (results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                // VULNERABILITY: XSS (CWE-79)
                out.println("Found: " + sr.getName());
            }
            ctx.close();
        } catch (NamingException e) {
            // VULNERABILITY: Information Exposure (CWE-209)
            out.println("LDAP Error: " + e.getMessage());
        }
    }
}
