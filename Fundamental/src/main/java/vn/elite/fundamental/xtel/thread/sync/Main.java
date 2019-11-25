package vn.elite.fundamental.xtel.thread.sync;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Main {
    private static final String PREFIX_RESOURCE = String.format(".%1$sresource%1$sthreadsync%1$s", File.separator);

    private static final String INPUT_FILE_NAME = PREFIX_RESOURCE + "input.txt";
    private static final String SEARCH_FILE_NAME = PREFIX_RESOURCE + "search.txt";
    private static final String OUTPUT_FILE_NAME = PREFIX_RESOURCE + "output.txt";

    private static volatile int search;

    static {
        System.out.println(PREFIX_RESOURCE);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try {
            Input input = new Input();
            int[] source = input.load(INPUT_FILE_NAME);
            int[] searches = input.load(SEARCH_FILE_NAME);
            int searchValue = searches[0];

            Processing p = new Processing(source);

            Thread sorting = new Thread(p::sort);
            Thread searching = new Thread(() -> search = p.search(searchValue));
            Thread outputting = new Thread(() -> p.save(OUTPUT_FILE_NAME, search));

            sorting.start();
            searching.start();
            outputting.start();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        long stop = System.currentTimeMillis();
        System.out.println("Time: " + (stop - start) + "ms");
    }
}
