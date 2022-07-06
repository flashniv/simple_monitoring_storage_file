package ua.com.serverhelp.simplemetricstoragefile.filedriver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class FileDriverTest {
    private final String metric = "test.stage.db.booleanitem1";
    @Autowired
    private FileDriver fileDriver;
    @Value("${metric-storage.metrics-directory}")
    private String dirName;

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
    @AfterEach
    void tearDown() {
        File file = new File(dirName);
        Assertions.assertTrue(deleteDirectory(file));
    }

    @Test
    void writeMetric() {
        List<DataElement> dataElements = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            DataElement dataElement = new DataElement(new Event("test.stage.db.booleanitem1", "{}", Instant.now().getEpochSecond(), Math.random()));
            dataElements.add(dataElement);
        }
        Assertions.assertDoesNotThrow(() -> fileDriver.writeMetric(metric, dataElements));


        Assertions.assertDoesNotThrow(() -> {
            List<DataElement> dataElements1 = fileDriver.readFile(metric);
            Assertions.assertEquals(dataElements, dataElements1);
        });

    }
}