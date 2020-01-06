package vn.elite.fundamental.crypto;

import lombok.val;
import vn.elite.core.crypto.MD5;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class MD5Test {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        val hash = MD5.hash("日本".getBytes());
        System.out.println(hash);
    }
}
