package vn.elite.snatcher.test;

import lombok.val;

import javax.tools.ToolProvider;

import static vn.elite.core.utils.StringUtils.escape;
import static vn.elite.core.utils.StringUtils.unescape;

public class Test {
    public static void main(String[] args) {
        //language=RegExp
        String s = "https://imgur\\.com/a/\\w+";

        System.out.println(escape(s));
        System.out.println(unescape(s));

        val compiler = ToolProvider.getSystemJavaCompiler();

        for (val version : compiler.getSourceVersions()) {
            System.out.println(version);
        }
    }
}
