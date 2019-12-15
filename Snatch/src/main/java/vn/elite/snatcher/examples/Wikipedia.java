package vn.elite.snatcher.examples;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static org.jsoup.Jsoup.connect;

/**
 * A simple example, used on the jsoup website.
 */
@Slf4j
public class Wikipedia {
    public static void main(String[] args) throws IOException {
        Document doc = connect("http://en.wikipedia.org/")
            .proxy("10.61.57.22", 3128)
            .get();
        log.info(doc.title());
        Elements newsHeadlines = doc.select("#mp-itn b a");
        for (Element headline : newsHeadlines) {
            log.info("{}\n{}", headline.attr("title"), headline.absUrl("href"));
        }
    }
}
