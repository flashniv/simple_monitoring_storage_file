package ua.com.serverhelp.simplemetricstoragefile.utils.maintenance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

class ClearFileStorageDBTest extends AbstractTest {

    @Test
    void clearFiles() throws IOException {
        LocalDate now = LocalDate.now();
        String[] metrics = {"metric1", "metric2", "metric3", "metric4", "metric5"};

        for (String metric : metrics) {
            String metricMD5 = DigestUtils.md5DigestAsHex(metric.getBytes());
            for (int i = 0; i < 18; i++) {
                String fileName = dirName + "/" + metricMD5.substring(0, 2) + "/" + metricMD5.substring(2, 4) + "/" + metricMD5 + "_" + now.getYear() + "_" + now.minusDays(i).getMonthValue() + "_" + now.minusDays(i).getDayOfMonth();

                File file = new File(fileName);
                //noinspection ResultOfMethodCallIgnored
                file.getParentFile().mkdirs();
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            }
        }
        String metricMD5 = DigestUtils.md5DigestAsHex("metric6".getBytes());
        String fileName = dirName + "/" + metricMD5.substring(0, 2) + "/" + metricMD5.substring(2, 4) + "/" + metricMD5 + "_" + now.getYear() + "_" + now.minusDays(30).getMonthValue() + "_" + now.minusDays(30).getDayOfMonth();
        File file = new File(fileName);
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();

        clearFileStorageDB.clearFiles();
        Assertions.assertEquals(71, fileDriver.getAllFiles().size());
    }
}