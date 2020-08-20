package org.abondar.experimental.webcrawler;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Crawler {

    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);
    private static final String HTTP_KEY = "http";
    private static final String GOOGLE_KEY = "google";
    private static String SEARCH_URL = "https://www.google.com/search?q=";

    public static void setSearchUrl(String searchUrl) {
        SEARCH_URL = searchUrl;
    }

    public void Crawl(String term) {
        var links = downloadFromGoogle(term);

        if (links.isEmpty()) {
            System.out.println("No links fetched");
        } else {
            downloadFromLinks(links);
        }

    }

    private Set<String> downloadFromGoogle(String term) {
        Set<String> res = new HashSet<>();
        try {
            var doc = Jsoup.connect(SEARCH_URL + term).get();
            var links = doc.select("a[href]");

            for (var l : links) {
                String link = l.attr("href");
                if (link.startsWith(HTTP_KEY) && !link.contains(GOOGLE_KEY)) {
                    res.add(link);
                }
            }

            return filterLinks(res);

        } catch (IOException ex) {
            logger.error("Failed to retrieve links from google");
            return res;
        }
    }

    private Set<String> filterLinks(Set<String> unfiltered) {

        Set<String> fitlered = new HashSet<>();

        unfiltered.forEach(l->{
            try {
                fitlered.add(new URL(l).getHost());
            } catch (MalformedURLException e) {
                logger.error("Failed to parse link");

            }
        });

        return fitlered;
    }

    private void downloadFromLinks(Set<String> links){

    }
}
