package org.abondar.experimental.webcrawler;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CrawlerTest {

    @Test
    public void countResultsTest() {
        var crawler = new Crawler();
        var libs = List.of("vue.js", "vue.js", "ember.js", "angular.js", "react.js", "backbone.js");

        var res = crawler.countResults(libs);
        assertEquals(5, res.size());
        assertEquals("vue.js", res.get(0));
    }


    @Test
    public void countResultsShortTest() {
        var crawler = new Crawler();
        var libs = List.of("vue.js", "vue.js", "ember.js", "angular.js", "react.js");

        var res = crawler.countResults(libs);
        assertEquals(4, res.size());
        assertEquals("vue.js", res.get(0));
    }


    @Test
    public void downloadLinkTest() throws Exception {
        var crawler = new Crawler();
        var link = "http://www.yandex.ru";
        var res = crawler.downloadLink(link);
        assertEquals(4, res.size());


    }



    @Test
    @Disabled
    //TODO: fix JSOUP mocking
    public void downloadLinkMockTest() throws Exception {
        var input = new File((this.getClass().getClassLoader().getResource("yandex.html").getFile()));
        var doc = Jsoup.parse(input, "UTF-8");
        var crawler = new Crawler();
        var link = "http://www.url.ru";

        var conn = mock(Connection.class);
        var resp = mock(Connection.Response.class);
        when(conn.execute()).thenReturn(resp);

        when(conn.get()).thenReturn(doc);


        try (MockedStatic<Jsoup> mock = mockStatic(Jsoup.class)) {

            mock.when(() -> Jsoup.connect(link).get()).thenReturn(conn);
            var res = crawler.downloadLink(link);
            assertEquals(4, res.size());
        }

    }


    @Test
    public void filterLinksTest(){
        var links = Set.of("http://www.test.de",
                "http://www.test.de/media",
                "http://www.test.de/search",
                "http://www.vk.com",
                "http://ru.wikipedia.org");

        var crawler = new Crawler();
        var res = crawler.filterLinks(links);

        assertEquals(3,res.size());
    }

    @Test
    //TODO: replace with mock test after understanding how to mock jsoup
    public void downloadFromGoogleTest(){
        var term = "test";
        var crawler = new Crawler();

        var res = crawler.downloadFromGoogle(term);
        assertEquals(7,res.size());
        assertEquals("https://www.test.de",res.iterator().next());
    }

    @Test
    public void crawlTest(){
        var term = "test";
        var crawler = new Crawler();

        var res = crawler.Crawl(term);
        assertEquals(5,res.size());
        assertEquals("embed.js",res.get(0));
    }

    @Test
    public void crawlPerformanceTest(){
        var term = "test";
        var crawler = new Crawler();
        var start = System.currentTimeMillis();
        crawler.Crawl(term);
        var perf = System.currentTimeMillis() - start;


        assertTrue(perf<=10000);

    }
}
