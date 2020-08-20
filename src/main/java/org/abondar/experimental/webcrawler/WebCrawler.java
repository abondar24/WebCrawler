package org.abondar.experimental.webcrawler;

import java.util.Scanner;

public class WebCrawler {

    public static void main(String[] args) {
        var crawler = new Crawler();
        var scanner = new Scanner(System.in);
        var searchTerm = scanner.nextLine();
        var res = crawler.Crawl(searchTerm);
        System.out.printf("Top 5 JavaScript Libraries by term %s\n",searchTerm);
        res.forEach(System.out::println);
        //TODO: create new thread for each term
//         while (true){
//
//         }
    }
}
