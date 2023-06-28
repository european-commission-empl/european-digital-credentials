package eu.europa.ec.empl.edci.config.service;

import org.apache.jena.http.HttpEnv;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ProxyConfigService extends BaseConfigService {

    private HttpClient defaultHttpClient = null;
    private HttpClient defaultHttpsClient = null;

    @PostConstruct
    public void postConstruct() {
        Boolean httpEnabled = Boolean.valueOf(getString("proxy.http.enabled", "false"));
        Boolean httpsEnabled = Boolean.valueOf(getString("proxy.https.enabled", "false"));
        String httpUser = this.getString("proxy.http.user", "").isBlank() ? null : getString("proxy.http.user");
        String httpPwd = this.getString("proxy.http.pwd", "").isBlank() ? null : getString("proxy.http.pwd");
        String httpsUser = this.getString("proxy.https.user", "").isBlank() ? null : getString("proxy.https.user");
        String httpsPwd = this.getString("proxy.https.pwd", "").isBlank() ? null : getString("proxy.https.pwd");
        String httpHost = getString("proxy.http.host", "").isBlank() ? null : getString("proxy.http.host");
        Integer httpPort = getInteger("proxy.http.port", null);
        String httpsHost = getString("proxy.https.host", "").isBlank() ? null : getString("proxy.https.host");
        Integer httpsPort = getInteger("proxy.https.port", null);

        if (httpEnabled) {
            if (httpUser != null && httpPwd != null) {
                System.setProperty("http.proxyUser", httpUser);
                System.setProperty("http.proxyPassword", httpPwd);
            }

            if (httpHost != null && httpPort != null) {
                System.setProperty("http.proxyHost", httpHost);
                System.setProperty("http.proxyPort", String.valueOf(httpPort));
            }
        }


        if (httpsEnabled) {
            if (httpsHost != null && httpsPort != null) {
                System.setProperty("https.proxyHost", httpsHost);
                System.setProperty("https.proxyPort", String.valueOf(httpsPort));
            }

            if (httpsUser != null && httpsPwd != null) {
                System.setProperty("https.proxyUser", httpsUser);
                System.setProperty("https.proxyPassword", httpsPwd);
            }
        }

        if (hasHttpAuthentication() || hasHttpsAuthentication()) {
            //SET DEFAULT FOR AUTHENTICATOR, SET DEFAULT FOR JENA SPARQL THROUGH HTTPENV
            System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
            Authenticator authenticator = this.getDefaultHttpAuthenticator();
            HttpClient httpClient = getDefaultHttpClient();
            HttpEnv.setDftHttpClient(httpClient);
            Authenticator.setDefault(authenticator);
            ProxySelector.setDefault(this.getProxySelector());
        }

    }

    public Authenticator getDefaultHttpAuthenticator() {
        String httpUser = this.getString("proxy.http.user", null);
        String httpPwd = this.getString("proxy.http.pwd", null);
        return new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(httpUser, httpPwd.toCharArray());
            }
        };
    }

    public Authenticator getDefaultHttpsAuthenticator() {
        String httpUser = this.getString("proxy.https.user", null);
        String httpPwd = this.getString("proxy.https.pwd", null);
        return new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(httpUser, httpPwd.toCharArray());
            }
        };
    }


    public HttpClient getDefaultHttpClient() {
        Long timeout = Long.valueOf(this.getString("http.https.timeout.seconds", "10"));
        if (this.defaultHttpClient == null) {
            HttpClient.Builder clientBuilder = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(timeout))
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .proxy(this.getProxySelector());
            if (hasHttpAuthentication()) {
                clientBuilder.authenticator(getDefaultHttpAuthenticator());
            }
            this.defaultHttpClient = clientBuilder.build();
        }

        return this.defaultHttpClient;
    }

    public HttpClient getDefaultHttpsClient() {
        Long timeout = Long.valueOf(this.getString("http.https.timeout.seconds", "10"));
        if (this.defaultHttpsClient == null) {
            HttpClient.Builder clientBuilder = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(timeout))
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .proxy(this.getProxySelector());
            if (hasHttpsAuthentication()) {
                clientBuilder.authenticator(getDefaultHttpsAuthenticator());
            }
            this.defaultHttpsClient = clientBuilder.build();
        }
        return this.defaultHttpsClient;
    }

    private boolean hasHttpAuthentication() {
        Boolean httpEnabled = Boolean.valueOf(getString("proxy.http.enabled", "false"));
        if (httpEnabled) {
            String httpUser = this.getString("proxy.http.user", null);
            String httpPwd = this.getString("proxy.http.pwd", null);
            if ((httpUser != null && !httpUser.isBlank()) && (httpPwd != null && !httpPwd.isBlank())) {
                return true;
            }
        } else {
            return false;
        }

        return false;
    }

    private boolean hasHttpsAuthentication() {
        Boolean httpsEnabled = Boolean.valueOf(getString("proxy.https.enabled", "false"));
        if (httpsEnabled) {
            String httpsUser = this.getString("proxy.https.user", null);
            String httpsPwd = this.getString("proxy.https.pwd", null);
            if ((httpsUser != null && !httpsUser.isBlank()) && (httpsPwd != null && !httpsPwd.isBlank())) {
                return true;
            }
        } else {
            return false;
        }

        return false;
    }

    private ProxySelector getProxySelector() {
        String httpHost = getString("proxy.http.host", "").isBlank() || !getBoolean("proxy.http.enabled", false)
                ? null : getString("proxy.http.host");
        Integer httpPort = getInteger("proxy.http.port", null);
        String httpsHost = getString("proxy.https.host", "").isBlank() || !getBoolean("proxy.https.enabled", false)
                ? null : getString("proxy.https.host");
        Integer httpsPort = getInteger("proxy.https.port", null);
        String internalUrlRegex = getString("proxy.noproxy.regex.url", "").isBlank() ? null : getString("proxy.noproxy.regex.url");
        return new EDCIProxySelector(httpHost, httpPort, httpsHost, httpsPort, internalUrlRegex);
    }

    private class EDCIProxySelector extends ProxySelector {
        private ProxySelector httpProxySelector = null;
        private ProxySelector httpsProxySelector = null;
        private Pattern internalUrlRegex = null;

        public EDCIProxySelector(String httpHost, Integer httpPort, String httpsHost, Integer httpsPort, String internalUrlRegex) {
            if (httpHost != null && httpPort != null) {
                httpProxySelector = ProxySelector.of(new InetSocketAddress(getString("proxy.http.host"), getInteger("proxy.http.port")));
            }
            if (httpsHost != null && httpsPort != null) {
                httpsProxySelector = ProxySelector.of(new InetSocketAddress(getString("proxy.https.host"), getInteger("proxy.https.port")));
            }
            if (internalUrlRegex != null) {
                this.internalUrlRegex = Pattern.compile(internalUrlRegex);
            }
        }

        @Override
        public List<Proxy> select(URI uri) {
            if (internalUrlRegex != null && internalUrlRegex.matcher(uri.toString()).find()) {
                return Arrays.asList(Proxy.NO_PROXY);
            }
            if (httpProxySelector == null && httpsProxySelector == null) {
                return Arrays.asList(Proxy.NO_PROXY);
            }
            if (httpsProxySelector == null) {
                return httpProxySelector.select(uri);
            }
            if (httpProxySelector == null) {
                return httpsProxySelector.select(uri);
            }
            if (uri.getScheme().equalsIgnoreCase("http")) {
                return httpProxySelector.select(uri);
            } else {
                return httpsProxySelector.select(uri);
            }
        }

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            if (httpProxySelector == null && httpsProxySelector == null) {
                ProxySelector.getDefault().connectFailed(uri, sa, ioe);
            } else {
                if (uri.getScheme().equalsIgnoreCase("http")) {
                    if (httpProxySelector != null) {
                        httpProxySelector.connectFailed(uri, sa, ioe);
                    } else {
                        httpsProxySelector.connectFailed(uri, sa, ioe);
                    }
                } else {
                    if (httpsProxySelector != null) {
                        httpsProxySelector.connectFailed(uri, sa, ioe);
                    } else {
                        httpProxySelector.connectFailed(uri, sa, ioe);
                    }
                }
            }
        }
    }
}
