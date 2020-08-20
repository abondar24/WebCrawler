package org.abondar.experimental.webcrawler;

import java.util.Scanner;

public class WebCrawler {

    public static void main(String[] args) {
         var crawler = new Crawler();
         var scanner = new Scanner(System.in);
        var searchTerm = scanner.nextLine();
        crawler.Crawl(searchTerm);
//         while (true){
//
//         }
    }
}
