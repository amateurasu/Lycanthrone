package vn.elite.snatcher.examples;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static org.jsoup.Jsoup.connect;

/**
 * Example program to list links from a URL.
 */
@Slf4j
public class ListLinks {
    public static void main(String[] args) throws IOException {
        Validate.isTrue(args.length == 1, "usage: supply url to fetch");
        String url = args[0];
        log.info("Fetching {}...", url);

        Document doc = connect(url)
            .proxy("10.61.57.22", 3128)
            .get();
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

        log.info("Media: ({})", media.size());
        for (Element src : media) {
            if (src.tagName().equals("img")) {
                log.info(" * {}: <{}> {}x{} ({})",
                    src.tagName(),
                    src.attr("abs:src"),
                    src.attr("width"),
                    src.attr("height"),
                    trim(src.attr("alt"), 20));
            } else {
                log.info(" * {}: <{}>", src.tagName(), src.attr("abs:src"));
            }
        }

        log.info("\nImports: ({})", imports.size());
        for (Element link : imports) {
            log.info(" * {} <{}> ({})", link.tagName(), link.attr("abs:href"), link.attr("rel"));
        }

        log.info("\nLinks: ({})", links.size());
        for (Element link : links) {
            log.info(" * a: <{}>  ({})", link.attr("abs:href"), trim(link.text(), 35));
        }
    }

    private static String trim(String s, int width) {
        return s.length() > width
            ? s.substring(0, width - 1) + "."
            : s;
    }
}
