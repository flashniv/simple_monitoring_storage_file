package ua.com.serverhelp.simplemetricstoragefile.filedriver;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileDriver {
    @Setter
    @Value("${metric-storage.metrics-directory}")
    private String dirName;

    private String getPeriod() {
        LocalDate now = LocalDate.now();
        return now.getYear() + "_" + now.getMonthValue() + "_" + now.getDayOfMonth();
    }

    public void writeMetric(String metricName, List<DataElement> dataElements) throws IOException {
        String metricMD5 = DigestUtils.md5DigestAsHex(metricName.getBytes());
        String fileName = dirName + "/" + metricMD5.substring(0, 2) + "/" + metricMD5.substring(2, 4) + "/" + metricMD5 + "_" + getPeriod();

        File file = new File(fileName);
        file.getParentFile().mkdirs();
        if (file.createNewFile()) {
            log.info("FileDriver::writeMetric file " + fileName + " created for metric " + metricName);
        }
        FileOutputStream fos = new FileOutputStream(file, true);
        DataOutputStream dos = new DataOutputStream(fos);

        for (DataElement dataElement : dataElements) {
            // write object to file
            dos.writeLong(dataElement.getTimestamp());
            dos.writeDouble(dataElement.getValue());
        }
        dos.close();
        log.debug("FileDriver::writeMetric Metric " + metricName + " was write. Events=" + dataElements.size());
    }

    public List<DataElement> readMetric(String metricName, Instant begin, Instant end) throws IOException, ClassNotFoundException {
        String metricMD5 = DigestUtils.md5DigestAsHex(metricName.getBytes());
        String currentDirName = dirName + "/" + metricMD5.substring(0, 2) + "/" + metricMD5.substring(2, 4);
        List<DataElement> dataElements = new ArrayList<>();

        List<Path> files = Files.list(Paths.get(currentDirName))
                .filter(file -> Files.isRegularFile(file) && file.toString().contains(metricMD5))
                .collect(Collectors.toList());

        for (Path file : files) {
            DataInputStream dis = new DataInputStream(Files.newInputStream(file));

            while (dis.available() > 0) {
                long timestamp = dis.readLong();
                double value = dis.readDouble();
                //check time range
                if (timestamp > end.getEpochSecond() && timestamp <= begin.getEpochSecond()) {
                    DataElement dataElement = new DataElement();

                    dataElement.setTimestamp(timestamp);
                    dataElement.setValue(value);

                    dataElements.add(dataElement);
                }
            }
            dis.close();
        }

        log.debug("FileDriver::readMetric Metric " + metricName + " was read.");
        return dataElements.stream().sorted(Comparator.comparingLong(DataElement::getTimestamp)).collect(Collectors.toList());
    }

    public List<DataElement> readMetric(String metricName) throws IOException, ClassNotFoundException {
        return readMetric(metricName, Instant.ofEpochSecond(1), Instant.now());
    }


}
