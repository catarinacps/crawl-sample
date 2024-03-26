package com.crawl.backend.crawler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class CrawlerServiceTest {
    @Spy
    CrawlerService crawlerService = new CrawlerService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crawlLinkTree() {
        when(crawlerService.getWebsiteContent(any()))
                .thenReturn(Optional.of("something\n<a href=\"http://localhost:8080/hey\">a link</a>"))
                .thenReturn(Optional.of("another thing\n<p>hahha</p>\n<a href=\"document.html\">another link</a>\n<a href=\"http://not.this.one.link.though\">link to somewhere else</a>"))
                .thenReturn(Optional.of("final document thing"))
                .thenReturn(Optional.of("this thing should not be reached"));

        crawlerService.setBaseUrl(URI.create("http://localhost:8080"));

        assertEquals(crawlerService.find(Pattern.compile(".*")).size(), 3);
    }
}