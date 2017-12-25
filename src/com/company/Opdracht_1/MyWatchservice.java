package com.company.Opdracht_1;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

import java.util.logging.*;

public class MyWatchservice {
    public static void main(String[] args) {
        final WatchService watcher;
        Path dir = Paths.get("C:\\temp");
        WatchKey key;

        try {
            watcher = FileSystems.getDefault().newWatchService();
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            while (true) {
                key = watcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;

                    Path filename = ev.context();
                    Path child = dir.resolve(filename);

                    WatchEvent.Kind kind = ev.kind();
                    if (kind == ENTRY_CREATE) {
                        System.out.println(child + " created");
                        new MyFileReader(dir.toAbsolutePath().toString() + filename.toString()).run();
                    }
                    else if (kind == ENTRY_DELETE) {
                        System.out.println(child + " deleted");
                    }
                    else if (kind == ENTRY_MODIFY) {
                        System.out.println(child + " modified");
                        new MyFileReader(dir.toAbsolutePath().toString() + '\\' + filename.toString()).run();
                    }
                }
                key.reset();
            }

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(MyWatchservice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
