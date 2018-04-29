package com.heraj.ssl.server.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@ConditionalOnProperty("server.ssl.enabled2way")
public class MutualAuthServerConfig {
    
    @Bean
    public SSLContext create2WaySSLContext(@Value("${ssl.overrideDefault:true}") boolean overrideDefault) throws Exception
    {
        
        String keystorePath = "serverkeystore.p12";
        String keystorePassword = "123456";
        String truststorePath = "servertruststore.p12";
        String truststorePassword = "123456";
        
        log.info("Configuring 2-way SSL with keystore: '{}' and truststore: '{}'", keystorePath, truststorePath);
        KeyStore keyStore = createKeyStore(keystorePath, keystorePassword);
        KeyManager[] keyManagers = createKeyManagers(keyStore, keystorePassword);

        TrustManager[] trustmanagers = StringUtils.isEmpty(truststorePath)
                ? new TrustManager[]{ createTrustAllTrustManager() }
                : createTrustManagerFactory(truststorePath, truststorePassword).getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustmanagers, new SecureRandom());

        if (overrideDefault) {
            SSLContext.setDefault(sslContext);
        }

        log.info("Configured 2-way SSL");
        return sslContext;
    }
    
    private static TrustManagerFactory createTrustManagerFactory(String truststorePath, String truststorePassword) throws GeneralSecurityException, IOException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore truststore = createKeyStore(truststorePath, truststorePassword);
        tmf.init(truststore);
        return tmf;
    }

    private static KeyStore _createDefaultKeyStore(String keystorePath, String keystorePassword) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream inputStream = getKeyStoreInputStream(keystorePath);
        keyStore.load(inputStream, keystorePassword.toCharArray());
        return keyStore;
    }

    private static KeyStore _createKeyMaterialKeyStore(String keystorePath, String keystorePassword) throws GeneralSecurityException, IOException {
        InputStream keystoreLocation = getKeyStoreInputStream(keystorePath);
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream inputStream = getKeyStoreInputStream(keystorePath);
        keyStore.load(inputStream, keystorePassword.toCharArray());
        return keyStore;
    }

    private static KeyStore createKeyStore(String keystorePath, String keystorePassword) throws GeneralSecurityException, IOException {
        KeyStore keyStore;
        try {
            keyStore = _createDefaultKeyStore(keystorePath, keystorePassword);
        } catch (Exception e) {
            // Using <code>_createDefaultKeyStore()</code> with a PKCS12 keystore may result in a ConcurrentModifiationException,
            // in order to avoid that, we're using the ca.juliusdavies:not-yet-commons-ssl library here.
            // See https://www.tbs-certificates.co.uk/FAQ/en/626.html
            keyStore = _createKeyMaterialKeyStore(keystorePath, keystorePassword);
        }

        logKeystore(keyStore, keystorePath);

        return keyStore;
    }

    private static void logKeystore(KeyStore keystore, String keystorePath) throws KeyStoreException {
        log.info("Keystore '{}' has {} entries", keystorePath, keystore.size());
        for (Enumeration<String> e = keystore.aliases(); e.hasMoreElements(); ) {
            log.info("Keystore alias: {}", e.nextElement());
        }
    }

    private static KeyManager[] createKeyManagers(KeyStore keystore, String keystorePassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, keystorePassword.toCharArray());
        return kmfactory.getKeyManagers();
    }

    private static InputStream getKeyStoreInputStream(String keystorePath) throws FileNotFoundException {
        File keystoreFile = new File(keystorePath);
        InputStream keystoreLocation = keystoreFile.exists() && keystoreFile.isFile()
                ? new FileInputStream(keystoreFile)
                : MutualAuthServerConfig.class.getResourceAsStream(keystorePath);

        if (keystoreLocation == null) {
            throw new IllegalStateException("No keystore found at " + keystorePath);
        }
        return keystoreLocation;
    }

    private static X509TrustManager createTrustAllTrustManager() {
        return new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        };
    }

}
