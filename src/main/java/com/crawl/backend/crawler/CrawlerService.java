package com.crawl.backend.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CrawlerService {
    private final ConcurrentHashMap<URI, String> fileTree = new ConcurrentHashMap<>();
    private final Pattern anchorPattern = Pattern.compile("<a(?:\\s+href=['\"]([[\\w\\p{Punct}]&&[^'\"]]+?)['\"]|\\s+\\w+=['\"][[\\w\\p{Punct}]&&[^\"]]+?['\"])*>(?:.(?!</a>))*.</a>");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private URI baseUrl;

    public CrawlerService() {
        logger.warn("New CrawlerService instantiated with no URL");
    }

    public CrawlerService(URI baseUrl) throws MalformedURLException {
        this.baseUrl = baseUrl;

        if (Objects.isNull(this.baseUrl)) {
            logger.warn("New CrawlerService instantiated with no URL");
            return;
        }

        if (!baseUrl.isAbsolute() || !baseUrl.getScheme().contains("http")) {
            logger.error("Malformed base URL {}", baseUrl);
            throw new MalformedURLException();
        }

        logger.info("New CrawlerService instantiated and initialized with given URL {}", baseUrl);

        this.crawl(baseUrl);

        this.fileTree.values().removeIf(String::isEmpty);

        logger.debug("Initial crawl finished, found {} documents", fileTree.size());
    }

    public ConcurrentMap<URI, Long> find(String word) {
        return this.find(Pattern.compile(word, Pattern.CASE_INSENSITIVE), 0);
    }

    public ConcurrentMap<URI, Long> find(Pattern pattern) {
        return this.find(pattern, 0);
    }

    public ConcurrentMap<URI, Long> find(Pattern pattern, int group) {
        if (Objects.isNull(this.baseUrl)) {
            logger.warn("This instance's base URL is null, ignoring operation");
            return null;
        }

        var hits = fileTree.entrySet()
                .parallelStream()
                .collect(Collectors.toConcurrentMap(
                        Entry::getKey,
                        e -> this.findPatternInContent(e.getValue(), pattern, group)
                                .count()));

        hits.values().removeIf(l -> l == 0);

        return hits;
    }

    public Optional<String> getWebsiteContent(URI uri) {
        if (Objects.isNull(uri)) {
            return Optional.empty();
        }

        URL url;

        try {
            url = uri.toURL();
        } catch (MalformedURLException e) {
            logger.warn("Failed to convert given URL {} to an URL object", uri);
            return Optional.empty();
        }

        HttpURLConnection connection;

        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            logger.error("Failed to initialize URL connection of {}", url);
            return Optional.empty();
        }

        try {
            connection.setRequestMethod("GET");
            connection.connect();
        } catch (IOException e) {
            logger.error("Failed to perform connection {}", connection);
            return Optional.empty();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            var responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            return Optional.of(responseBuilder.toString());
        } catch (IOException e) {
            logger.error("Failed to read URL {} content", url);
            return Optional.empty();
        } finally {
            connection.disconnect();
        }
    }

    private boolean isUriInBaseDomain(URI uri) {
        return Optional.ofNullable(uri)
                .map(URI::getHost)
                .map(host -> host.equals(baseUrl.getHost()))
                .orElse(false);
    }

    private void crawl(URI uri) {
        Optional.ofNullable(uri)
                .filter(u -> Objects.isNull(fileTree.putIfAbsent(u, "")))
                .flatMap(this::getWebsiteContent)
                .stream()
                .parallel()
                .peek(content -> fileTree.put(uri, content))
                .flatMap(this::findHrefInHTMLAnchors)
                .peek(links -> logger.debug("found this link {}", links))
                .map(URI::create)
                .distinct()
                .filter(Predicate.not(URI::isOpaque))
                .map(baseUrl::resolve)
                .filter(this::isUriInBaseDomain)
                .filter(Predicate.not(fileTree::containsKey))
                .forEach(this::crawl);
    }

    private Stream<String> findPatternInContent(String content, Pattern pattern, int group) {
        return pattern.matcher(content)
                .results()
                .parallel()
                .map(res -> res.group(group))
                .filter(Objects::nonNull)
                .filter(Predicate.not(String::isEmpty));
    }

    private Stream<String> findHrefInHTMLAnchors(String page) {
        return this.findPatternInContent(page, anchorPattern, 1);
    }

    public URI getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URI baseUrl) {
        this.baseUrl = baseUrl;

        this.fileTree.clear();

        this.crawl(baseUrl);

        this.fileTree.values().removeIf(String::isEmpty);
    }
}