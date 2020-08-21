package org.abondar.experimental.webcrawler;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class WebCrawler {

    private static final int THREAD_NUM = 4;

    public static void main(String[] args) throws Exception{

        var scanner = new Scanner(System.in);


        var executor = initExecutor();
        int counter = 0;

        while (true){
            System.out.println("Enter search term");
            var searchTerm = scanner.nextLine();
            counter ++;

            if (counter<THREAD_NUM){
                var future = executor.submit(getCallable(searchTerm));

                future.get().forEach(System.out::println);
            } else {
                executor.shutdown();
                counter=0;
                System.out.println("Reached max number of threads");
                executor = initExecutor();
            }


        }
    }

    private static ThreadPoolExecutor initExecutor(){
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_NUM);
    }

    private static Callable<List<String>> getCallable(String term){
        return () -> {
            System.out.printf("Top 5 JavaScript Libraries by term %s\n",term);
            return new Crawler().Crawl(term);
        };
    }
}
