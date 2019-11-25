package vn.elite.snatcher.examples;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static org.jsoup.Jsoup.connect;

/**
 * A simple example, used on the jsoup website.
 */
public class H2read {
    public static void main(String[] args) throws IOException {
        //.proxy("10.61.60.2", 8888)
        Document doc = connect("https://hentai2read.com/watashi_no_suki_na_ojisan/").get();
        log(doc.title());
        System.out.println(doc);
        Elements newsHeadlines = doc.select("a.pull-left.font-w600");
        for (Element headline : newsHeadlines) {
            log("%s\n\t%s", headline.attr("title"), headline.absUrl("href"));
        }
    }

    private static void log(String msg, String... vals) {
        System.out.format(msg, vals);
    }
}
