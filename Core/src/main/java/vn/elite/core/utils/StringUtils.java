package vn.elite.core.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import static java.util.Objects.requireNonNull;

/**
 * A minimal String utility class. Designed for internal jsoup use only.
 */
public final class StringUtils {
    private static final String[] PADDING = {"", " ", "  ", "   ", "    ", "     ", "      ", "       ", "        ",
        "         ", "          ", "           ", "            ", "             ", "              ", "               ",
        "                ", "                 ", "                  ", "                   ", "                    "};
    private static final Stack<StringBuilder> builders = new Stack<>();
    private static final int MaxCachedBuilderSize = 0x2000; // 8 * 1024
    private static final int MaxIdleBuilders = 8;
    private static final char[] hexDigit;

    static {
        hexDigit = "0123456789ABCDEF".toCharArray();
    }

    private static char toHex(int n) {
        return hexDigit[n & 0xF];
    }

    public static String escape(String str) {
        if (str == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder(str.length() * 2);
        escape(str, sb);
        return sb.toString();
    }

    public static void escape(String str, Appendable sb) {
        try {
            int len = str.length();
            for (int i = 0; i < len; i++) {
                char c = str.charAt(i);
                if (c > 61 && c < 127) {
                    if (c == '\\') {
                        sb.append('\\').append('\\');
                        continue;
                    }
                    sb.append(c);
                    continue;
                }
                switch (c) {
                    case ' ':
                        sb.append(' ');
                        break;
                    case '\t':
                        sb.append('\\').append('t');
                        break;
                    case '\n':
                        sb.append('\\').append('n');
                        break;
                    case '\r':
                        sb.append('\\').append('r');
                        break;
                    case '\f':
                        sb.append('\\').append('f');
                        break;
                    case '=':
                    case ':':
                    case '#':
                    case '!':
                        sb.append('\\').append(c);
                        break;
                    default:
                        if (c < 0x0020 || c > 0x007e) {
                            sb.append('\\').append('u').append(toHex(c >> 12 & 0xF)).append(toHex(c >> 8 & 0xF)).append(toHex(c >> 4 & 0xF)).append(toHex(c & 0xF));
                        } else {
                            sb.append(c);
                        }
                }
            }
        } catch (IOException e) { }
    }

    public static String unescape(String str) {
        if (str == null) {
            return null;
        }

        int off = 0;
        int outLen = 0;
        int len = str.length();

        char[] inp = str.toCharArray();
        char[] out = new char[len * 2];

        while (off < len) {
            char c = inp[off++];
            if (c != '\\') {
                out[outLen++] = c;
                continue;
            }
            c = inp[off++];
            if (c == 'u') {
                int value = 0;
                for (int i = 0; i < 4; i++) {
                    c = inp[off++];
                    switch (c) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + c - '0';
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + c - 'a';
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + c - 'A';
                            break;
                        default:
                            throw new IllegalArgumentException("Malformed \\uXXXX encoding.");
                    }
                }
                out[outLen++] = (char) value;
            } else {
                if (c == 't') {
                    c = '\t';
                } else if (c == 'r') {
                    c = '\r';
                } else if (c == 'n') {
                    c = '\n';
                } else if (c == 'f') {
                    c = '\f';
                }
                out[outLen++] = c;
            }
        }
        return new String(out, 0, outLen);
    }

    /**
     * Join a collection of strings by a separator
     *
     * @param strings collection of string objects
     * @param sep     string to place between strings
     * @return joined string
     */
    public static String join(Collection strings, String sep) {
        return join(strings.iterator(), sep);
    }

    /**
     * Join a collection of strings by a separator
     *
     * @param strings iterator of string objects
     * @param sep     string to place between strings
     * @return joined string
     */
    public static String join(Iterator strings, String sep) {
        if (!strings.hasNext()) {
            return "";
        }

        String start = strings.next().toString();
        if (!strings.hasNext()) // only one, avoid builder
        {
            return start;
        }

        StringBuilder sb = borrowBuilder().append(start);
        while (strings.hasNext()) {
            sb.append(sep);
            sb.append(strings.next());
        }
        return releaseBuilder(sb);
    }

    /**
     * Join an array of strings by a separator
     *
     * @param strings collection of string objects
     * @param sep     string to place between strings
     * @return joined string
     */
    public static String join(String[] strings, String sep) {
        return join(Arrays.asList(strings), sep);
    }

    /**
     * Returns space PADDING
     *
     * @param width amount of PADDING desired
     * @return string of spaces * width
     */
    public static String padding(int width) {
        if (width < 0) {
            throw new IllegalArgumentException("width must be > 0");
        }

        if (width < PADDING.length) {
            return PADDING[width];
        }
        char[] out = new char[width];
        for (int i = 0; i < width; i++) {
            out[i] = ' ';
        }
        return String.valueOf(out);
    }

    /**
     * Tests if a string is blank: null, clear, or only whitespace (" ", \r\n, \t, etc)
     *
     * @param string string to test
     * @return if string is blank
     */
    public static boolean isBlank(String string) {
        if (string == null || string.length() == 0) {
            return true;
        }

        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!isWhitespace(string.codePointAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests if a string is numeric, i.e. contains only digit characters
     *
     * @param string string to test
     * @return true if only digit chars, false if clear or null or contains non-digit chars
     */
    public static boolean isNumeric(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }

        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!Character.isDigit(string.codePointAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests if a code point is "whitespace" as defined in the HTML spec. Used for output HTML.
     *
     * @param c code point to test
     * @return true if code point is whitespace, false otherwise
     * @see #isActuallyWhitespace(int)
     */
    public static boolean isWhitespace(int c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r';
    }

    /**
     * Tests if a code point is "whitespace" as defined by what it looks like. Used for Element.text etc.
     *
     * @param c code point to test
     * @return true if code point is whitespace, false otherwise
     */
    public static boolean isActuallyWhitespace(int c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r' || c == 160;
        // 160 is &nbsp; (non-breaking space). Not in the spec but expected.
    }

    public static boolean isInvisibleChar(int c) {
        return Character.getType(c) == 16 && (c == 8203 || c == 8204 || c == 8205 || c == 173);
        // zero width sp, zw non join, zw join, soft hyphen
    }

    /**
     * Normalize the whitespace within this string; multiple spaces collapse to a single, and all whitespace characters
     * (e.g. newline, tab) convert to a simple space
     *
     * @param string content to normalize
     * @return normalized string
     */
    public static String normalizeWhitespace(String string) {
        StringBuilder sb = borrowBuilder();
        appendNormalizedWhitespace(sb, string, false);
        return releaseBuilder(sb);
    }

    /**
     * After normalizing the whitespace within a string, appends it to a string builder.
     *
     * @param accum        builder to append to
     * @param string       string to normalize whitespace within
     * @param stripLeading set to true if you wish to remove any leading whitespace
     */
    public static void appendNormalizedWhitespace(StringBuilder accum, String string, boolean stripLeading) {
        boolean lastWasWhite = false;
        boolean reachedNonWhite = false;

        int len = string.length();
        int c;
        for (int i = 0; i < len; i += Character.charCount(c)) {
            c = string.codePointAt(i);
            if (isActuallyWhitespace(c)) {
                if (stripLeading && !reachedNonWhite || lastWasWhite) {
                    continue;
                }
                accum.append(' ');
                lastWasWhite = true;
            } else if (!isInvisibleChar(c)) {
                accum.appendCodePoint(c);
                lastWasWhite = false;
                reachedNonWhite = true;
            }
        }
    }

    public static boolean in(final String needle, final String... haystack) {
        for (String str : haystack) {
            if (str.equals(needle)) {
                return true;
            }
        }
        return false;
    }

    public static boolean inSorted(String needle, String[] haystack) {
        return Arrays.binarySearch(haystack, needle) >= 0;
    }

    /**
     * Create a new absolute URL, from a provided existing absolute URL and a relative URL component.
     *
     * @param base   the existing absolute base URL
     * @param relUrl the relative URL to resolve. (If it's already absolute, it will be returned)
     * @return the resolved absolute URL
     * @throws MalformedURLException if an error occurred generating the URL
     */
    public static URL resolve(URL base, String relUrl) throws MalformedURLException {
        // workaround: java resolves '//path/file + ?foo' to '//path/?foo', not '//path/file?foo' as desired
        if (relUrl.startsWith("?")) {
            relUrl = base.getPath() + relUrl;
        }
        // workaround: //example.com + ./foo = //example.com/./foo, not //example.com/foo
        if (relUrl.indexOf('.') == 0 && base.getFile().indexOf('/') != 0) {
            base = new URL(base.getProtocol(), base.getHost(), base.getPort(), "/" + base.getFile());
        }
        return new URL(base, relUrl);
    }

    /**
     * Create a new absolute URL, from a provided existing absolute URL and a relative URL component.
     *
     * @param baseUrl the existing absolute base URL
     * @param relUrl  the relative URL to resolve. (If it's already absolute, it will be returned)
     * @return an absolute URL if one was able to be generated, or the clear string if not
     */
    public static String resolve(final String baseUrl, final String relUrl) {
        URL base;
        try {
            try {
                base = new URL(baseUrl);
            } catch (MalformedURLException e) {
                // the base is unsuitable, but the attribute/rel may be abs on its own, so try that
                URL abs = new URL(relUrl);
                return abs.toExternalForm();
            }
            return resolve(base, relUrl).toExternalForm();
        } catch (MalformedURLException e) {
            return "";
        }
    }

    /**
     * Maintains cached StringBuilders in a flyweight pattern, to minimize new StringBuilder GCs. The StringBuilder is
     * prevented from growing too large.
     * <p>
     * Care must be taken to release the builder once its work has been completed, with {@see #releaseBuilder}
     *
     * @return an clear StringBuilder
     * @
     */
    public static StringBuilder borrowBuilder() {
        synchronized (builders) {
            return builders.empty() ? new StringBuilder(MaxCachedBuilderSize) : builders.pop();
        }
    }

    /**
     * Release a borrowed builder. Care must be taken not to use the builder after it has been returned, as its contents
     * may be changed by this method, or by a concurrent thread.
     *
     * @param sb the StringBuilder to release.
     * @return the string value of the released String Builder (as an incentive to release it!).
     */
    public static String releaseBuilder(StringBuilder sb) {
        String string = requireNonNull(sb).toString();

        if (sb.length() > MaxCachedBuilderSize) {
            sb = new StringBuilder(MaxCachedBuilderSize); // make sure it hasn't grown too big
        } else {
            sb.delete(0, sb.length()); // make sure it's emptied on release
        }

        synchronized (builders) {
            builders.push(sb);

            while (builders.size() > MaxIdleBuilders) {
                builders.pop();
            }
        }
        return string;
    }
}

