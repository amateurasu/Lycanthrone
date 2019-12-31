package vn.elite.fundamental.java.reflect;

import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class Annotation {
    public static void main(String[] args) {
        URL[] urls = getUrlsForCurrentClasspath();
        Stream.of(urls).forEach(url -> log.info(url.toString()));
        System.out.println();

        Runtime runtime = Runtime.getRuntime();


        int processors = runtime.availableProcessors();
        System.out.println(processors);

        runtime.addShutdownHook(new Thread(() -> System.out.println("shutdown hook running")));
    }

    private static URL[] getUrlsForCurrentClasspath() {
        List<URL> list = new ArrayList<>();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        while (loader != null) {
            log.info(loader.getClass().getCanonicalName());
            if (loader instanceof URLClassLoader) {
                URL[] urlArray = ((URLClassLoader) loader).getURLs();
                List<URL> urlList = Arrays.asList(urlArray);
                list.addAll(urlList);
            }
            loader = loader.getParent();
        }
        return list.toArray(new URL[0]);
    }
}
