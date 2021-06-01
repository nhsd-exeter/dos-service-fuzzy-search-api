package uk.nhs.digital.uec.api.auth.factory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.function.Supplier;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/** The class allows a Spring RestTemplate to communicate with an SSL REST endpoint. */
public class SslFactorySupplier implements Supplier<ClientHttpRequestFactory> {

  @Override
  public ClientHttpRequestFactory get() {
    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
    SSLContext sslContext;
    try {
      sslContext =
          org.apache.http.ssl.SSLContexts.custom()
              .loadTrustMaterial(null, acceptingTrustStrategy)
              .build();
    } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
      throw new IllegalStateException("Could not set up SSL Context", e);
    }
    SSLConnectionSocketFactory csf =
        new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
    HttpComponentsClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory();
    requestFactory.setHttpClient(httpClient);
    return requestFactory;
  }
}
