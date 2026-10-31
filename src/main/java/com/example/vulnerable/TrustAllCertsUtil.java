package com.example.vulnerable;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;

/**
 * VULNERABILITY: Improper Certificate Validation (CWE-295) - HIGH
 * SSL/TLS certificate and hostname validation is completely disabled,
 * making the application vulnerable to Man-in-the-Middle attacks.
 */
public class TrustAllCertsUtil {

    // VULNERABILITY: TrustManager that accepts all certificates (CWE-295) - HIGH
    private static final TrustManager[] TRUST_ALL = new TrustManager[]{
        new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}  // no-op
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}  // no-op
        }
    };

    // VULNERABILITY: HostnameVerifier that accepts all hostnames (CWE-297) - HIGH
    private static final HostnameVerifier TRUST_ALL_HOSTNAMES =
            (hostname, session) -> true;  // always returns true

    public static String fetchUrl(String targetUrl) throws Exception {
        // Install the trust-all manager globally - affects the entire JVM
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, TRUST_ALL, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // VULNERABILITY: Disable hostname verification (CWE-297)
        HttpsURLConnection.setDefaultHostnameVerifier(TRUST_ALL_HOSTNAMES);

        URL url = new URL(targetUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
