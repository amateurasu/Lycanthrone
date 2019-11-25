package vn.elite.fundamental;

import lombok.val;

import java.io.File;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Test {
    private static int id = 0;

    private static int getId() {
        return ++id;
    }

    public static void main(String[] args) {
        switch (getId()) {
            case 0:
                System.out.println(0);
                break;
            case 1:
                System.out.println(1);
                break;
            case 2:
                System.out.println(2);
                break;
            case 3:
                System.out.println(3);
        }

        if (true) return;
        int mb = 1024 * 1024;

        val runtime = Runtime.getRuntime();
        System.out.println("Total Memory: " + runtime.totalMemory() / mb);
        System.out.println("Free Memory: " + runtime.freeMemory() / mb);
        System.out.println("Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / mb);
        System.out.println("Max Memory: " + runtime.maxMemory() / mb);

        String[] lis = Objects.requireNonNull(new File("").list());
        Arrays.stream(lis).forEach(s -> {
            File file = new File(s);
            System.out.println(file);
        });


        System.setProperty("java.net.useSystemProxies", "true");
        System.out.println("detecting proxies");
        try {
            List l = ProxySelector.getDefault().select(new URI("http://foo/bar"));

            if (l != null) {
                for (Object o : l) {
                    Proxy proxy = (Proxy) o;
                    System.out.println("proxy type: " + proxy.type());

                    InetSocketAddress addr = (InetSocketAddress) proxy.address();

                    if (addr == null) {
                        System.out.println("No Proxy");
                    } else {
                        System.out.println("proxy hostname: " + addr.getHostName());
                        System.setProperty("http.proxyHost", addr.getHostName());
                        System.out.println("proxy port: " + addr.getPort());
                        System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
                    }
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        I a = null;

        List<I> list = Arrays.asList(new A(), new B());
        I i = list.get(0);
        i.set("haghdvaghvwdhvahwvdyh");
        System.out.println(list);

        list.set(0, new A());
        System.out.println(list);

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
            System.out.println("A - public static void fn()");
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
            System.out.println("A - public static void fn()");
        }
    }

}
