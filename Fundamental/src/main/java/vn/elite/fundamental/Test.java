package vn.elite.fundamental;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class Test {
    public static void main(String[] args) {
        int mb = 1024 * 1024;

        val runtime = Runtime.getRuntime();
        log.info("Total Memory: {}", runtime.totalMemory() / mb);
        log.info("Free Memory:  {}", runtime.freeMemory() / mb);
        log.info("Used Memory:  {}", (runtime.totalMemory() - runtime.freeMemory()) / mb);
        log.info("Max Memory:   {}", runtime.maxMemory() / mb);

        String[] lis = Objects.requireNonNull(new File("./").list());
        Arrays.stream(lis).forEach(s -> log.info(new File(s).getAbsolutePath()));

        System.setProperty("java.net.useSystemProxies", "true");
        log.info("detecting proxies");
        try {
            List<Proxy> l = ProxySelector.getDefault().select(new URI("http://foo/bar"));

            for (Object o : l) {
                Proxy proxy = (Proxy) o;
                log.info("proxy type: " + proxy.type());

                InetSocketAddress addr = (InetSocketAddress) proxy.address();

                if (addr == null) {
                    log.info("No Proxy");
                } else {
                    log.info("proxy hostname: " + addr.getHostName());
                    System.setProperty("http.proxyHost", addr.getHostName());
                    log.info("proxy port: " + addr.getPort());
                    System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        I a = null;

        List<I> list = Arrays.asList(new A(), new B());
        I i = list.get(0);

        log.info(list.toString());

        list.set(0, new A());
        log.info(list.toString());

        A.fn();
        C.fn();
    }

    interface I {
        String get();

        void set(String i);
    }

    public static class A implements I {
        private String a = "String a";

        public static void fn() {
            log.info("A - public static void fn()");
        }

        @Override
        public String get() {
            return a;
        }

        @Override
        public void set(String i) {
            a = i;
        }

        @Override
        public String toString() {
            return String.format("A{a='%s'}", a);
        }
    }

    public static class B implements I {
        private String b;

        @Override
        public String get() {
            return b;
        }

        @Override
        public void set(String i) {
            b = i;
        }

        @Override
        public String toString() {
            return "B{b='" + b + "'}";
        }
    }

    public static class C extends A {
        public static void fn() {
            log.info("A - public static void fn()");
        }
    }
}
