package vn.elite.fundamental.java.version;

/**
 * Java Finder by petrucio@stackoverflow(828681) is licensed under a Creative Commons Attribution 3.0 Unported License.
 * Needs WinRegistry.java. Get it at: https://stackoverflow.com/questions/62289/read-write-to-windows-registry-using-java
 * <p>
 * JavaFinder - Windows-specific classes to search for all installed versions of java on this system Author:
 * petrucio@stackoverflow (828681)
 *****************************************************************************/
public class JvmVersion {
    public static double JAVA_VERSION = getVersion();

    private static double getVersion() {
        String version = System.getProperty("java.version");
        int pos = version.indexOf('.');
        pos = version.indexOf('.', pos + 1);
        return Double.parseDouble(version.substring(0, pos));
    }

    public static void main(String[] args) {
        System.out.println(JvmVersion.JAVA_VERSION);
    }
}






