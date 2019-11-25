package vn.elite.fundamental.xtel.phone;

import java.io.IOException;

public class Tesuto {

    private static final short CONTENT = 3;
    private static final String DELIMITER = ",";

    private static final String FILE_PREFIX = "xtel_phonefilter\\resource\\phonefilter\\";
    private static final String FILE_ORIGIN = FILE_PREFIX + "origin.txt";
    private static final String FILE_OUT = FILE_PREFIX + "hcm_full.txt";
    private static final String FILE_DISTINCT = FILE_PREFIX + "hcm_distinct.txt";
    private static final String FILE_SPAM = FILE_PREFIX + "hcm_spam.txt";

    private static final String CONDITION = "hcm,hochiminh,hochjm,chimj,chjmj,chimi,thpho,tpho,thanhph,thahph," +
        "xosohcm,soxohcm,sxohcm,xsohcm,sosohcm,xshcm,sxhcm,sshcm,xxhcm,cauhcm,soihcm,soicauhcm,tkhcm,kqhcm,kqxshcm," +
        "xsmnhcm,xosotp,soxotp,sxotp,xsotp,sosotp,xxtp,sxtp,sstp,xxtp,cautp,soitp,soicautp,tktp,kqtp,kqxstp,xsmntp";

    public static void main(String[] args) {
        try {
            System.gc();
            long start = System.currentTimeMillis();
            FileUtils.reMapFile(FILE_ORIGIN, DELIMITER, CONTENT, CONDITION, FILE_OUT);
            System.out.format("%-30s %10d ms", "Filter first time:", System.currentTimeMillis() - start);

            long current = System.currentTimeMillis();
            FileUtils.reMapFile(FILE_OUT, FILE_DISTINCT);
            System.out.format("%-30s %10d ms\n", "Filter distinct:", System.currentTimeMillis() - current);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
