package vn.elite.fundamental.java.properties;

import lombok.val;
import vn.elite.core.utils.StringUtils;

import java.util.Locale;
import java.util.ResourceBundle;

public class Bundle {
    public static void main(String[] args) {
        val resource = "vn/elite/fundamental/bundle/i18n";
        ResourceBundle rb = ResourceBundle.getBundle(resource, Locale.getDefault());
        System.out.println(rb.getString("some.value"));

        ResourceBundle rb2 = ResourceBundle.getBundle(resource, new Locale("vi", "VN"));
        System.out.println(StringUtils.unescape(rb2.getString("some.value")));
        rb2.keySet().forEach(System.out::println);
    }
}
