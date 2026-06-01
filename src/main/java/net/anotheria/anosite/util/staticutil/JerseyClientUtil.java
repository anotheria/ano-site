package net.anotheria.anosite.util.staticutil;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import jakarta.ws.rs.client.Client;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * Util for configuring jersey client.
 *
 * @author ykalapusha
 */
public final class JerseyClientUtil {
    /**
     * Jersey {@link Client} client instance.
     */
    private static final Client CLIENT;

    /**
     * Configure jersey client
     */
    static {
        TrustManager[] certs = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }
                }
        };

        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to get SSL context. " + e.getMessage());
        }
        try {
            ctx.init(null, certs, null);
        } catch (KeyManagementException e) {
            throw new RuntimeException("Unable to init SSL context. " + e.getMessage());
        }

        CLIENT = new JerseyClientBuilder()
                .hostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier())
                .register(MultiPartFeature.class)
                .sslContext(ctx)
                .connectTimeout(5_000, TimeUnit.MILLISECONDS)
                .readTimeout(90_000, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * Default constructor.
     */
    private JerseyClientUtil() { }

    /**
     * Getter for jersey client
     *
     * @return
     *      {@link Client} configured jersey client
     */
    public static Client getClientInstance() {
        return CLIENT;
    }
}

