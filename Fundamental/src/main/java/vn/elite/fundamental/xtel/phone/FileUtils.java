package vn.elite.fundamental.xtel.phone;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class FileUtils {

    public static void reMapFile(String fileSrc, String fileDesc) throws IOException {
        if (fileSrc == null) {
            throw new NullPointerException("File src is null");
        }

        File fSrc = new File(fileSrc);
        if (!fSrc.exists()) {
            throw new FileNotFoundException("File: " + fileSrc + " not found.");
        }

        if (fileDesc == null) {
            fileDesc = fSrc.getAbsolutePath() + ".remap";
        }

        try (
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileDesc));
            Stream<String> sStream = Files.lines(Paths.get(fileSrc))
        ) {
            sStream.map(v -> v.split(",")[0]).forEach(x -> {
                try {
                    writer.write(x + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.flush();
        }
    }

    public static void reMapFile84(String fileSrc, String fileDesc) throws IOException {
        if (fileSrc == null) {
            throw new NullPointerException("File src is null");
        }

        File fSrc = new File(fileSrc);
        if (!fSrc.exists()) {
            throw new FileNotFoundException("File: " + fileSrc + " not found.");
        }

        if (fileDesc == null) {
            fileDesc = fSrc.getAbsolutePath() + ".remap";
        }

        try (
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileDesc));
            Stream<String> sStream = Files.lines(Paths.get(fileSrc))
        ) {
            sStream.map(v -> v.split(",")[0]).forEach(x -> {
                try {
                    if (!x.startsWith("84")) {
                        x = "84" + x;
                    }
                    writer.write(x + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.flush();
        }
        //if (writer != null)
    }

    public static void reMapFile(String fileSrc, String separator, int index, String condition, String fileDesc) throws IOException {
        if (fileSrc == null) {
            throw new NullPointerException("File src is null");
        }

        File fSrc = new File(fileSrc);
        if (!fSrc.exists()) {
            throw new FileNotFoundException("File: " + fileSrc + " not found.");
        }

        if (fileDesc == null) {
            fileDesc = fSrc.getAbsolutePath() + ".remap";
        }

        String[] conditions = condition.split(",");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileDesc))) {
            Files.lines(Paths.get(fileSrc)).map(v -> {
                String[] ss = v.split(separator);
                String s = ss[0];
                String s1 = ss[index].replace("\\s", "").toLowerCase();
                for (String x : conditions) {
                    if (s1.contains(x.toLowerCase())) {
                        return s;
                    }
                }
                return null;
            }).filter(Objects::nonNull).forEach(x -> {
                try {
                    writer.write(x + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.flush();
        }
        //if (writer != null)
    }

    public static void distinct(String fileSrc, String fileDesc) throws IOException {
        if (fileSrc == null) {
            throw new NullPointerException("File src is null");
        }

        File fSrc = new File(fileSrc);
        if (!fSrc.exists()) {
            throw new FileNotFoundException("File: " + fileSrc + " not found.");
        }

        if (fileDesc == null) {
            fileDesc = fSrc.getAbsolutePath() + ".distinct";
        }

        try (
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileDesc));
            Stream<String> sStream = Files.lines(Paths.get(fileSrc))
        ) {
            sStream.distinct().forEach(x -> {
                try {
                    writer.write(x + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.flush();
        }
    }

    public static void innerJoin(String fileSrc, String fileSrc1, String fileDest) throws IOException {
        if (fileSrc == null || fileSrc1 == null) {
            throw new NullPointerException("File src or src1 is null");
        }

        File fSrc = new File(fileSrc);
        if (!fSrc.exists()) {
            throw new FileNotFoundException("File: " + fileSrc + " not found.");
        }

        File fSrc1 = new File(fileSrc1);
        if (!fSrc1.exists()) {
            throw new FileNotFoundException("File: " + fileSrc1 + " not found.");
        }

        if (fileDest == null) {
            fileDest = fSrc.getAbsolutePath() + ".join";
        }

        try (
            BufferedWriter br = new BufferedWriter(new FileWriter(fileDest));
            Stream<String> s1 = Files.lines(Paths.get(fileSrc1)); Stream<String> s2 = Files.lines(Paths.get(fileSrc))
        ) {

            final Set<String> table1 = new HashSet<>();

            s1.forEach(table1::add);

            s2.filter(table1::contains).forEach(x -> {
                try {
                    br.write(x + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            br.flush();
        }
    }

    public static void _2NotIn1(String fileSrc1, String fileSrc2, String fileDest) throws IOException {
        if (fileSrc1 == null || fileSrc2 == null) {
            throw new NullPointerException("File src or src1 is null");
        }

        File fSrc = new File(fileSrc1);
        if (!fSrc.exists()) {
            throw new FileNotFoundException("File: " + fileSrc1 + " not found.");
        }

        File fSrc1 = new File(fileSrc2);
        if (!fSrc1.exists()) {
            throw new FileNotFoundException("File: " + fileSrc2 + " not found.");
        }

        if (fileDest == null) {
            fileDest = fSrc1.getAbsolutePath() + ".2NotIn1";
        }

        try (
            BufferedWriter br = new BufferedWriter(new FileWriter(fileDest));
            Stream<String> s1 = Files.lines(Paths.get(fileSrc1)); Stream<String> s2 = Files.lines(Paths.get(fileSrc2))
        ) {

            final Set<String> table1 = new HashSet<>();
            s1.forEach(table1::add);

            s2.filter(x -> !table1.contains(x)).forEach(x -> {
                try {
                    br.write(x + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            br.flush();
        }
    }
}
