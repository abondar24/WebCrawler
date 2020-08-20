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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class Crawler {

    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);
    private static final String HTTP_KEY = "http";
    private static final String GOOGLE_KEY = "google";

    private static final String HTTP_DELIMITER = "://";
    private static final String SCRIPT_KEY = "script";
    private static final String SRC_KEY = "src";
    private static String SEARCH_URL = "https://www.google.com/search?q=";

    private static final String DELIMITER = "/";

    private static final String JS_EXTENSION = "js";


    public static void setSearchUrl(String searchUrl) {
        SEARCH_URL = searchUrl;
    }

    public List<String> Crawl(String term) {
        var links = downloadFromGoogle(term);

        if (links.isEmpty()) {
            logger.info("No links fetched");

            return List.of();
        } else {
            return downloadLinks(links);
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

        unfiltered.forEach(l -> {
            try {
                var url = new URL(l);
                fitlered.add(url.getProtocol() + HTTP_DELIMITER + url.getHost());
            } catch (MalformedURLException e) {
                logger.error(e.getMessage());

            }
        });

        return fitlered;
    }

    private List<String> downloadLinks(Set<String> links) {
        List<String> jsLibs = new ArrayList<>();
        links.forEach(l -> jsLibs.addAll(downloadLink(l)));

        return filterResults(jsLibs);
    }


    private List<String> downloadLink(String link) {

        var executor = Executors.newSingleThreadExecutor();

        var callable = new Callable<List<String>>() {

            @Override
            public List<String> call() {
                List<String> jsLibs = new ArrayList<>();
                try {
                    var scripts = Jsoup
                            .connect(link)
                            .get()
                            .getElementsByTag(SCRIPT_KEY);

                    for (var s : scripts) {
                        var jsLib = s.attr(SRC_KEY);
                        if (!jsLib.isEmpty()) {
                            var tmp = jsLib.split(DELIMITER);
                            var lib = tmp[tmp.length - 1];

                            if (lib.contains(JS_EXTENSION)){
                                jsLibs.add(lib);
                            }


                        }
                    }
                } catch (IOException ex) {
                    logger.error(ex.getMessage());
                }
                return jsLibs;
            }
        };

        var future = executor.submit(callable);
        executor.shutdown();

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ex) {
            logger.error(ex.getMessage());
            return List.of();
        }
    }

    //TODO: implement filter
    private List<String> filterResults(List<String> jsLibs) {
        return jsLibs;
    }
}
