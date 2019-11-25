package vn.elite.eml.system.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileFilter {

    private static Path inputFolder;
    private ArrayList<Path> list;

    public FileFilter() {
        inputFolder = Paths.get("D:\\Projects\\Java\\Lycanthrone\\ProcessEml\\resource\\folder");
        //FileFilter.config = config;
    }

    public FileFilter(String path) {
        inputFolder = Paths.get(path);
    }

    public static void main(String[] args) {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path path1 = Paths.get("./"); // System.getProperty("user.home")
            System.out.println(path1.toAbsolutePath());
            Path path = Paths.get("D:\\Projects\\Java\\Lycanthrone\\ProcessEml\\resource\\folder");

            path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
            path1.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.printf("Event kind: %s. File affected: %s.%n", event.kind(), event.context());
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Path> getList() {
        return list;
    }

    public ArrayList<Path> fetchFileList() {
        list = new ArrayList<>();

        File[] files = inputFolder.toFile().listFiles((File dir, String name) -> name.endsWith(".eml"));
        if (files == null) {
            return list;
        }

        for (File file : files) {
            if (file.isFile()) {
                list.add(file.toPath());
            }
        }
        return list;
    }

    void registerRecursive(WatchService watchService, Path root) throws IOException {
        // register all subfolders
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
