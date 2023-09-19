package eu.europa.ec.empl.edci.util.proxy;

import eu.europa.ec.empl.edci.exception.EDCIException;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.net.ssl.HostnameVerifier;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.*;
import java.util.Map.Entry;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataLoaderUtil {
    //Self-reference to proc AOP cache
    @Autowired
    private DataLoaderUtil self;
    public static final Logger logger = LogManager.getLogger(DataLoaderUtil.class);
    private static final List<Integer> ACCEPTED_HTTP_STATUS = Arrays.asList(200);
    protected String contentType;
    private EDCIProxyConfig proxyConfig;
    private int timeoutConnection;
    private int timeoutSocket;
    private int connectionsMaxTotal;
    private int connectionsMaxPerRoute;
    private boolean redirectsEnabled;
    private List<Integer> acceptedHttpStatus;
    private final Map<HttpHost, UsernamePasswordCredentials> authenticationMap;
    private String sslProtocol;
    private String sslKeystorePath;
    private String sslKeystoreType;
    private String sslKeystorePassword;
    private boolean loadKeyStoreAsTrustMaterial;
    private String sslTruststorePath;
    private String sslTruststoreType;
    private String sslTruststorePassword;
    private TrustStrategy trustStrategy;
    private String[] supportedSSLProtocols;
    private String[] supportedSSLCipherSuites;
    private HostnameVerifier hostnameVerifier;
    private HttpRequestRetryHandler retryHandler;
    private ServiceUnavailableRetryStrategy serviceUnavailableRetryStrategy;

    public DataLoaderUtil() {
        this.timeoutConnection = 6000;
        this.timeoutSocket = 6000;
        this.connectionsMaxTotal = 20;
        this.connectionsMaxPerRoute = 2;
        this.redirectsEnabled = true;
        this.acceptedHttpStatus = ACCEPTED_HTTP_STATUS;
        this.authenticationMap = new HashMap();
        this.sslProtocol = "TLSv1.2";
        this.sslKeystoreType = KeyStore.getDefaultType();
        this.sslKeystorePassword = "";
        this.loadKeyStoreAsTrustMaterial = false;
        this.sslTruststoreType = KeyStore.getDefaultType();
        this.sslTruststorePassword = "";
        this.hostnameVerifier = SSLConnectionSocketFactory.getDefaultHostnameVerifier();
        this.contentType = contentType;
    }

    private HttpClientConnectionManager getConnectionManager() {
        RegistryBuilder<ConnectionSocketFactory> socketFactoryRegistryBuilder = RegistryBuilder.create();
        socketFactoryRegistryBuilder = this.setConnectionManagerSchemeHttp(socketFactoryRegistryBuilder);
        socketFactoryRegistryBuilder = this.setConnectionManagerSchemeHttps(socketFactoryRegistryBuilder);
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistryBuilder.build());
        connectionManager.setMaxTotal(this.connectionsMaxTotal);
        connectionManager.setDefaultMaxPerRoute(this.connectionsMaxPerRoute);
        logger.debug("PoolingHttpClientConnectionManager: max total: " + connectionManager.getMaxTotal());
        logger.debug("PoolingHttpClientConnectionManager: max per route: " + connectionManager.getDefaultMaxPerRoute());
        return connectionManager;
    }

    private RegistryBuilder<ConnectionSocketFactory> setConnectionManagerSchemeHttp(RegistryBuilder<ConnectionSocketFactory> socketFactoryRegistryBuilder) {
        return socketFactoryRegistryBuilder.register("http", PlainConnectionSocketFactory.getSocketFactory());
    }

    private RegistryBuilder<ConnectionSocketFactory> setConnectionManagerSchemeHttps(RegistryBuilder<ConnectionSocketFactory> socketFactoryRegistryBuilder) {
        try {
            SSLContextBuilder sslContextBuilder = SSLContextBuilder.create();
            sslContextBuilder.setProtocol(this.sslProtocol);
            KeyStore sslTrustStore = this.getSSLTrustStore();
            if (sslTrustStore != null) {
                logger.debug("Set the SSL trust store as trust materials");
                sslContextBuilder.loadTrustMaterial(sslTrustStore, this.getTrustStrategy());
            }

            KeyStore sslKeystore = this.getSSLKeyStore();
            if (sslKeystore != null) {
                logger.debug("Set the SSL keystore as key materials");
                char[] password = this.sslKeystorePassword != null ? this.sslKeystorePassword.toCharArray() : null;
                sslContextBuilder.loadKeyMaterial(sslKeystore, password);
                if (this.loadKeyStoreAsTrustMaterial) {
                    logger.debug("Set the SSL keystore as trust materials");
                    sslContextBuilder.loadTrustMaterial(sslKeystore, this.getTrustStrategy());
                }
            }

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(), this.getSupportedSSLProtocols(), this.getSupportedSSLCipherSuites(), this.getHostnameVerifier());
            return socketFactoryRegistryBuilder.register("https", sslConnectionSocketFactory);
        } catch (Exception var6) {
            throw new EDCIException(var6).addDescription("Unable to configure the SSLContext/SSLConnectionSocketFactory");
        }
    }

    protected KeyStore getSSLKeyStore() throws IOException, GeneralSecurityException {
        return this.loadKeyStore(this.sslKeystorePath, this.sslKeystoreType, this.sslKeystorePassword);
    }

    protected KeyStore getSSLTrustStore() throws IOException, GeneralSecurityException {
        return this.loadKeyStore(this.sslTruststorePath, this.sslTruststoreType, this.sslTruststorePassword);
    }

    private KeyStore loadKeyStore(String path, String type, String passwordStr) throws IOException, GeneralSecurityException {
        if (!StringUtils.isEmpty(path)) {
            FileInputStream is = new FileInputStream(path);

            KeyStore var7;
            try {
                KeyStore ks = KeyStore.getInstance(type);
                char[] password = passwordStr != null ? passwordStr.toCharArray() : null;
                ks.load(is, password);
                var7 = ks;
            } catch (Throwable var9) {
                try {
                    is.close();
                } catch (Throwable var8) {
                    var9.addSuppressed(var8);
                }

                throw var9;
            }

            is.close();
            return var7;
        } else {
            return null;
        }
    }

    protected synchronized HttpClientBuilder getHttpClientBuilder() {
        return HttpClients.custom();
    }

    protected synchronized CloseableHttpClient getHttpClient(String url) {
        HttpClientBuilder httpClientBuilder = this.getHttpClientBuilder();
        httpClientBuilder = this.configCredentials(httpClientBuilder, url);
        Builder custom = RequestConfig.custom();
        custom.setSocketTimeout(this.timeoutSocket);
        custom.setConnectTimeout(this.timeoutConnection);
        custom.setRedirectsEnabled(this.redirectsEnabled);
        RequestConfig requestConfig = custom.build();
        httpClientBuilder = httpClientBuilder.setDefaultRequestConfig(requestConfig);
        httpClientBuilder.setConnectionManager(this.getConnectionManager());
        httpClientBuilder.setRetryHandler(this.retryHandler);
        httpClientBuilder.setServiceUnavailableRetryStrategy(this.serviceUnavailableRetryStrategy);
        return httpClientBuilder.build();
    }

    private HttpClientBuilder configCredentials(HttpClientBuilder httpClientBuilder, String url) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        Iterator var4 = this.authenticationMap.entrySet().iterator();

        while (var4.hasNext()) {
            Entry<HttpHost, UsernamePasswordCredentials> entry = (Entry) var4.next();
            HttpHost httpHost = (HttpHost) entry.getKey();
            UsernamePasswordCredentials usernamePasswordCredentials = (UsernamePasswordCredentials) entry.getValue();
            AuthScope authscope = new AuthScope(httpHost.getHostName(), httpHost.getPort());
            credentialsProvider.setCredentials(authscope, usernamePasswordCredentials);
        }

        httpClientBuilder = httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        httpClientBuilder = this.configureProxy(httpClientBuilder, credentialsProvider, url);
        return httpClientBuilder;
    }

    private URL getURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException var3) {
            throw new EDCIException(var3).addDescription("Unable to create URL instance");
        }
    }

    private HttpClientBuilder configureProxy(HttpClientBuilder httpClientBuilder, CredentialsProvider credentialsProvider, String url) {
        if (this.proxyConfig == null) {
            return httpClientBuilder;
        } else {
            String protocol = this.getURL(url).getProtocol();
            boolean proxyHTTPS = "https".equalsIgnoreCase(protocol) && this.proxyConfig.getHttpsProperties() != null;
            boolean proxyHTTP = "http".equalsIgnoreCase(protocol) && this.proxyConfig.getHttpProperties() != null;
            EDCIProxyProperties proxyProps = null;
            if (proxyHTTPS) {
                logger.debug("Use proxy https parameters");
                proxyProps = this.proxyConfig.getHttpsProperties();
            } else {
                if (!proxyHTTP) {
                    return httpClientBuilder;
                }

                logger.debug("Use proxy http parameters");
                proxyProps = this.proxyConfig.getHttpProperties();
            }

            String proxyHost = proxyProps.getHost();
            int proxyPort = proxyProps.getPort();
            String proxyUser = proxyProps.getUser();
            String proxyPassword = proxyProps.getPassword();
            String proxyExcludedHosts = proxyProps.getExcludedHosts();
            if (!StringUtils.isEmpty(proxyUser) && !StringUtils.isEmpty(proxyPassword)) {
                AuthScope proxyAuth = new AuthScope(proxyHost, proxyPort);
                UsernamePasswordCredentials proxyCredentials = new UsernamePasswordCredentials(proxyUser, proxyPassword);
                credentialsProvider.setCredentials(proxyAuth, proxyCredentials);
            }

            logger.debug("proxy host/port: {} {} ", () -> proxyHost, () -> proxyPort);
            HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
            if (!StringUtils.isEmpty(proxyExcludedHosts)) {
                final String[] hosts = proxyExcludedHosts.split("[,; ]");
                HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy) {
                    public HttpRoute determineRoute(HttpHost host, HttpRequest request, HttpContext context) throws HttpException {
                        String hostname = host != null ? host.getHostName() : null;
                        if (hosts != null && hostname != null) {
                            String[] var5 = hosts;
                            int var6 = var5.length;

                            for (int var7 = 0; var7 < var6; ++var7) {
                                String h = var5[var7];
                                if (hostname.equalsIgnoreCase(h)) {
                                    return new HttpRoute(host);
                                }
                            }
                        }

                        return super.determineRoute(host, request, context);
                    }
                };
                httpClientBuilder.setRoutePlanner(routePlanner);
            }

            return httpClientBuilder.setProxy(proxy);
        }
    }

    public byte[] getHttpGetResponse(String url) {
        HttpGet httpRequest = null;
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient client = null;

        byte[] var6;
        try {
            URI uri = new URI(url.trim());
            httpRequest = new HttpGet(uri);
            if (this.contentType != null) {
                httpRequest.setHeader("Content-Type", this.contentType);
            }

            client = this.getHttpClient(url);
            httpResponse = this.getHttpResponse(client, httpRequest);
            var6 = this.readHttpResponse(httpResponse); //
        } catch (IOException | URISyntaxException var23) {
            throw new EDCIException(var23).addDescription("Unable to process GET call for url '" + url + "'");
        } finally {
            try {
                if (httpRequest != null) {
                    httpRequest.releaseConnection();
                }

                if (httpResponse != null) {
                    EntityUtils.consumeQuietly(httpResponse.getEntity());
                    IOUtils.closeQuietly(httpResponse);
                }
            } finally {
                IOUtils.closeQuietly(client);
            }

        }

        return var6;
    }

    protected CloseableHttpResponse getHttpResponse(CloseableHttpClient client, HttpUriRequest httpRequest) throws
            IOException {
        URI uri = httpRequest.getURI();
        HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);
        return client.execute(targetHost, httpRequest, localContext);
    }

    public byte[] readHttpResponse(CloseableHttpResponse httpResponse) throws IOException {
        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        String reasonPhrase = statusLine.getReasonPhrase();
        if (!this.acceptedHttpStatus.contains(statusCode)) {
            String reason = !StringUtils.isEmpty(reasonPhrase) ? " / reason : " + reasonPhrase : "";
            throw new IOException("Not acceptable HTTP Status (HTTP status code : " + statusCode + reason + ")");
        } else {
            HttpEntity responseEntity = httpResponse.getEntity();
            if (responseEntity == null) {
                throw new IOException("No message entity for this response");
            } else {
                return this.getContent(responseEntity);
            }
        }
    }

    protected byte[] getContent(HttpEntity responseEntity) throws IOException {
        InputStream content = responseEntity.getContent();

        byte[] var3;
        try {
            var3 = IOUtils.toByteArray(content);
        } catch (Throwable var6) {
            if (content != null) {
                try {
                    content.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }
            }

            throw var6;
        }

        if (content != null) {
            content.close();
        }

        return var3;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<Integer> getAcceptedHttpStatus() {
        return this.acceptedHttpStatus;
    }

    public void setAcceptedHttpStatus(List<Integer> acceptedHttpStatus) {
        this.acceptedHttpStatus = acceptedHttpStatus;
    }

    public EDCIProxyConfig getProxyConfig() {
        return this.proxyConfig;
    }

    public void setProxyConfig(EDCIProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    public void setSslKeystorePath(String sslKeystorePath) {
        this.sslKeystorePath = sslKeystorePath;
    }

    public void setKeyStoreAsTrustMaterial(boolean loadKeyStoreAsTrustMaterial) {
        this.loadKeyStoreAsTrustMaterial = loadKeyStoreAsTrustMaterial;
    }

    public void setSslKeystoreType(String sslKeystoreType) {
        this.sslKeystoreType = sslKeystoreType;
    }

    public void setSslKeystorePassword(String sslKeystorePassword) {
        this.sslKeystorePassword = sslKeystorePassword;
    }

    public void setSslTruststorePath(String sslTruststorePath) {
        this.sslTruststorePath = sslTruststorePath;
    }

    public void setSslTruststorePassword(String sslTruststorePassword) {
        this.sslTruststorePassword = sslTruststorePassword;
    }

    public void setSslTruststoreType(String sslTruststoreType) {
        this.sslTruststoreType = sslTruststoreType;
    }

    public void setRetryHandler(HttpRequestRetryHandler retryHandler) {
        this.retryHandler = retryHandler;
    }

    public void setServiceUnavailableRetryStrategy(ServiceUnavailableRetryStrategy
                                                           serviceUnavailableRetryStrategy) {
        this.serviceUnavailableRetryStrategy = serviceUnavailableRetryStrategy;
    }

    public String[] getSupportedSSLProtocols() {
        return this.supportedSSLProtocols;
    }

    public void setSupportedSSLProtocols(String[] supportedSSLProtocols) {
        this.supportedSSLProtocols = supportedSSLProtocols;
    }

    public String[] getSupportedSSLCipherSuites() {
        return this.supportedSSLCipherSuites;
    }

    public void setSupportedSSLCipherSuites(String[] supportedSSLCipherSuites) {
        this.supportedSSLCipherSuites = supportedSSLCipherSuites;
    }

    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public TrustStrategy getTrustStrategy() {
        return this.trustStrategy;
    }

    public void setTrustStrategy(TrustStrategy trustStrategy) {
        this.trustStrategy = trustStrategy;
    }
}
