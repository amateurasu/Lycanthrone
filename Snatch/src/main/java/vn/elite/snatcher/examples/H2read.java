package vn.elite.snatcher.examples;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

import static org.jsoup.Jsoup.connect;

/**
 * A simple example, used on the jsoup website.
 */
@Slf4j
public class H2read {
    public static void main(String[] args) throws IOException {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.61.57.22", 3128));
        Document doc = connect("https://hentai2read.com/watashi_no_suki_na_ojisan/")
            .proxy(proxy)
            .get();
        log.info(doc.title());
        Elements newsHeadlines = doc.select("a.pull-left.font-w600");
        for (Element headline : newsHeadlines) {
            String link = headline.absUrl("href");
            log.info("{} {}", headline.attr("title"), link);
            Document gallery = connect(link)
                .proxy(proxy)
                .get();
            log.info(gallery.title());
            log.info(gallery.outerHtml());
        }
    }
}
