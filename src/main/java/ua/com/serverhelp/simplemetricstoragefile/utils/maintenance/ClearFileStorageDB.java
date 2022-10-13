package ua.com.serverhelp.simplemetricstoragefile.utils.maintenance;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class ClearFileStorageDB {
    @Autowired
    private FileDriver fileDriver;
    @Value("${metric-storage.history-depth}")
    private long historyDepth = 0;

    public void clearFiles() {
        if (historyDepth == 0) return;
        try {
            log.info("Start housekeeping!");
            List<Path> paths = fileDriver.getAllFiles();
            List<Path> toDelete = new ArrayList<>();
            HashMap<String, List<Path>> hashMap = new HashMap<>();

            paths.forEach(path -> {
                String fileName = path.getFileName().toString();
                String[] parts = fileName.split("_");
                List<Path> metric = hashMap.getOrDefault(parts[0], new ArrayList<>());
                metric.add(path);
                hashMap.put(parts[0], metric);
            });
            hashMap.forEach((key, paths1) -> {
                for (int i = 0; i < paths1.size(); i++) {
                    if (i == paths1.size() - 1) continue;
                    Path path = paths1.get(i);
                    String fileName = path.getFileName().toString();
                    String[] parts = fileName.split("_");
                    LocalDate localDate = LocalDate.of(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                    Duration duration = Duration.between(localDate.atTime(0, 0).atZone(ZoneId.systemDefault()).toInstant(), Instant.now());
                    if (duration.getSeconds() > historyDepth) {
                        toDelete.add(path);
                    }
                }
            });
            for (Path path : toDelete) {
                fileDriver.removeFile(path);
                log.info("Clear storage. File " + path.toString() + " was deleted!");
            }
            log.info("Stop housekeeping!");
        } catch (IOException e) {
            log.error("Clear file storage archive error", e);
            Sentry.captureException(e);
        }
    }
}
