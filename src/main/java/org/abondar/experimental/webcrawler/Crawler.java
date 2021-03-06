package org.abondar.experimental.webcrawler;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Crawler {

    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);
    private static final String HTTP_KEY = "http";
    private static final String GOOGLE_KEY = "google";

    private static final String HTTP_DELIMITER = "://";
    private static final String SCRIPT_KEY = "script";
    private static final String SRC_KEY = "src";
    private static final String SEARCH_URL = "https://www.google.com/search?q=";

    private static final String DELIMITER = "/";

    private static final String JS_EXTENSION = "js";



    public List<String> Crawl(String term) {
        var links = downloadFromGoogle(term);

        if (links.isEmpty()) {
            logger.info("No links fetched");

            return List.of();
        } else {
            return downloadLinks(links);
        }

    }

    public Set<String> downloadFromGoogle(String term) {
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
            logger.error(ex.getMessage());
            return res;
        }
    }

    public Set<String> filterLinks(Set<String> unfiltered) {

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

    public List<String> downloadLinks(Set<String> links) {
        List<String> jsLibs = new ArrayList<>();
        links.forEach(l -> jsLibs.addAll(downloadLink(l)));

        return countResults(jsLibs);
    }


    public List<String> downloadLink(String link) {

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


    public List<String> countResults(List<String> jsLibs) {
        var libCount = new HashMap<String,Integer>();

        jsLibs.forEach(l->{
            if (!libCount.containsKey(l)){
                libCount.put(l,1);
            } else {
                var count = libCount.get(l);
                libCount.put(l, count + 1);
            }
        });

        var sortedLibCount = libCount.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1,e2) -> e1, LinkedHashMap::new));

        var libArr = new Map.Entry[sortedLibCount.size()];
        sortedLibCount.entrySet().toArray(libArr);

        List<String> res= new ArrayList<>();

        if (libArr.length>=5){
            res.add((String) libArr[0].getKey());
            res.add((String) libArr[1].getKey());
            res.add((String) libArr[2].getKey());
            res.add((String) libArr[3].getKey());
            res.add((String) libArr[4].getKey());
        } else {
            for (Map.Entry lib : libArr){
                res.add((String) lib.getKey());
            }
        }



        return res;
    }
}
