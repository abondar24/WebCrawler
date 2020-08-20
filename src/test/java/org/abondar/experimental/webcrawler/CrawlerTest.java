package org.abondar.experimental.webcrawler;

import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class CrawlerTest {

    @Test
    public void regexTest() throws Exception{
        URL url = new URL("https://www.test.de/");

        System.out.println(url.getHost());


    }
}
