package org.abondar.experimental.webcrawler;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {

    private static  String SEARCH_URL ="https://www.google.com/search?q=";
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);


    public void Crawl(String term){
          var links = downloadFromGoogle(term);

          if (links.isEmpty()){
              System.out.println("No links fetched");
              return;
          }

    }

    private List<String> downloadFromGoogle(String term){
        List<String> res = new ArrayList<>();
      //  try {
              var doc = Jsoup.connect(SEARCH_URL+term);
            System.out.println(doc);
    //    } catch (IOException ex){
     //       logger.error("Failed to retrieve links from google");
    //        return res;
    //    }



        return res;
    }

    public static void setSearchUrl(String searchUrl) {
        SEARCH_URL = searchUrl;
    }
}
