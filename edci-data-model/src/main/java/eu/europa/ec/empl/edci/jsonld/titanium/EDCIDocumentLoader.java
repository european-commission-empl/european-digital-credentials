package eu.europa.ec.empl.edci.jsonld.titanium;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.JsonLdErrorCode;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.document.RdfDocument;
import com.apicatalog.jsonld.http.DefaultHttpClient;
import com.apicatalog.jsonld.http.HttpResponse;
import com.apicatalog.jsonld.http.ProfileConstants;
import com.apicatalog.jsonld.http.link.Link;
import com.apicatalog.jsonld.http.media.MediaType;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.apicatalog.jsonld.loader.DocumentLoaderOptions;
import com.apicatalog.jsonld.uri.UriResolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EDCIDocumentLoader implements DocumentLoader {
    private static final String PLUS_JSON = "+json";
    private final Map<String, DocumentLoader> loaders = new LinkedHashMap();
    private int maxRedirections = 10;
    private Map<String, DefaultHttpClient> httpClient = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(EDCIDocumentLoader.class.getName());

    public EDCIDocumentLoader(java.net.http.HttpClient defaultHttpClient, java.net.http.HttpClient defaultHttpsClient) {
        this.httpClient.put("http", new DefaultHttpClient(defaultHttpClient));
        this.httpClient.put("https", new DefaultHttpClient(defaultHttpsClient));
    }

    public EDCIDocumentLoader set(String scheme, DocumentLoader loader) {
        this.loaders.put(scheme, loader);
        return this;
    }

    public Document loadDocument(final URI uri, final DocumentLoaderOptions options) throws JsonLdError {

        try {
            URI targetUri = uri;

            MediaType contentType = null;

            URI contextUri = null;

            for (int redirection = 0; redirection < maxRedirections; redirection++) {

                // 2.
                try (HttpResponse response = httpClient.get(uri.getScheme().toLowerCase()).send(targetUri, getAcceptHeader(options.getRequestProfile()))) {

                    // 3.
                    if (response.statusCode() == 301
                            || response.statusCode() == 302
                            || response.statusCode() == 303
                            || response.statusCode() == 307
                    ) {

                        final Optional<String> location = response.location();

                        if (location.isPresent()) {
                            targetUri = UriResolver.resolveAsUri(targetUri, location.get());
                            continue;
                        }

                        throw new JsonLdError(JsonLdErrorCode.LOADING_DOCUMENT_FAILED, "Header location is required for code [" + response.statusCode() + "].");
                    }

                    if (response.statusCode() != 200) {
                        throw new JsonLdError(JsonLdErrorCode.LOADING_DOCUMENT_FAILED, "Unexpected response code [" + response.statusCode() + "]");
                    }

                    final Optional<String> contentTypeValue = response.contentType();

                    if (contentTypeValue.isPresent()) {
                        contentType = MediaType.of(contentTypeValue.get());
                    }

                    final Collection<String> linkValues = response.links();

                    if (linkValues != null && !linkValues.isEmpty()) {

                        // 4.
                        if (contentType == null
                                || (!MediaType.JSON.match(contentType)
                                && !contentType.subtype().toLowerCase().endsWith(PLUS_JSON))
                        ) {

                            final URI baseUri = targetUri;

                            Optional<Link> alternate =
                                    linkValues.stream()
                                            .flatMap(l -> Link.of(l, baseUri).stream())
                                            .filter(l -> l.relations().contains("alternate")
                                                    && l.type().isPresent()
                                                    && MediaType.JSON_LD.match(l.type().get())
                                            )
                                            .findFirst();

                            if (alternate.isPresent()) {

                                targetUri = alternate.get().target();
                                continue;
                            }
                        }

                        // 5.
                        if (contentType != null
                                && !MediaType.JSON_LD.match(contentType)
                                && (MediaType.JSON.match(contentType)
                                || contentType.subtype().toLowerCase().endsWith(PLUS_JSON))
                        ) {

                            final URI baseUri = targetUri;

                            final List<Link> contextUris =
                                    linkValues.stream()
                                            .flatMap(l -> Link.of(l, baseUri).stream())
                                            .filter(l -> l.relations().contains(ProfileConstants.CONTEXT))
                                            .collect(Collectors.toList());

                            if (contextUris.size() > 1) {
                                throw new JsonLdError(JsonLdErrorCode.MULTIPLE_CONTEXT_LINK_HEADERS);

                            } else if (contextUris.size() == 1) {
                                contextUri = contextUris.get(0).target();
                            }
                        }
                    }

                    if (contentType == null) {
                        LOGGER.log(Level.WARNING, "GET on URL [{0}] does not return content-type header. Trying application/json.", uri);
                        contentType = MediaType.JSON;
                    }

                    return resolve(contentType, targetUri, contextUri, response);
                }
            }

            throw new JsonLdError(JsonLdErrorCode.LOADING_DOCUMENT_FAILED, "Too many redirections");

        } catch (IOException e) {
            throw new JsonLdError(JsonLdErrorCode.LOADING_DOCUMENT_FAILED, e);
        }

    }

    private final Document resolve(
            final MediaType guessType,
            final URI targetUri,
            final URI contextUrl,
            final HttpResponse response) throws JsonLdError, IOException {

        MediaType type = MediaType.JSON_LD;
        boolean isRdfDocument = false;
        if (JsonDocument.accepts(guessType)) {
            type = guessType;
        } else if (RdfDocument.accepts(guessType)) {
            type = guessType;
            isRdfDocument = true;
        }
        try (final InputStream is = response.body()) {
            final Document remoteDocument = !isRdfDocument ? JsonDocument.of(type, is) : RdfDocument.of(type, is);

            remoteDocument.setDocumentUrl(targetUri);

            remoteDocument.setContextUrl(contextUrl);

            return remoteDocument;
        }
    }

    public static final String getAcceptHeader() {
        return getAcceptHeader(null);
    }

    public static final String getAcceptHeader(final Collection<String> profiles) {
        final StringBuilder builder = new StringBuilder();

        builder.append(MediaType.JSON_LD.toString());

        if (profiles != null && !profiles.isEmpty()) {
            builder.append(";profile=\"");
            builder.append(String.join(" ", profiles));
            builder.append("\"");
        }

        builder.append(',');
        builder.append(MediaType.JSON.toString());
        builder.append(";q=0.9,*/*;q=0.1");
        return builder.toString();
    }

}
