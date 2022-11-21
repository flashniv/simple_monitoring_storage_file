package ua.com.serverhelp.simplemetricstoragefile.filedriver;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ExpressionException;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        //noinspection ResultOfMethodCallIgnored
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

    public DataElement readLastEventOfMetric(String metricName) throws IOException, ClassNotFoundException, ExpressionException {
        DataElement lastDataElement = new DataElement(0, 0.0);
        AtomicBoolean found = new AtomicBoolean(false);
        readMetricWithHook(metricName, dataElement -> {
            found.set(true);
            if (dataElement.getTimestamp() > lastDataElement.getTimestamp()) {
                lastDataElement.setTimestamp(dataElement.getTimestamp());
                lastDataElement.setValue(dataElement.getValue());
            }
        });
        if (!found.get()) {
            throw new ExpressionException("Metric not have any values", new Exception());
        }
        log.debug("Read element " + lastDataElement);
        return lastDataElement;
    }

    public List<DataElement> readMetric(String metricName, Instant begin, Instant end) throws IOException, ClassNotFoundException {
        List<DataElement> dataElements = new ArrayList<>();
        readMetricWithHook(metricName, dataElement -> {
            if (dataElement.getTimestamp() > begin.getEpochSecond() && dataElement.getTimestamp() <= end.getEpochSecond()) {
                dataElements.add(dataElement);
            }
        });
        return dataElements.stream().sorted(Comparator.comparingLong(DataElement::getTimestamp)).collect(Collectors.toList());
    }

    public void readMetricWithHook(String metricName, Consumer<DataElement> consumer) throws IOException, ClassNotFoundException {
        String metricMD5 = DigestUtils.md5DigestAsHex(metricName.getBytes());
        String currentDirName = dirName + "/" + metricMD5.substring(0, 2) + "/" + metricMD5.substring(2, 4);

//        Path dir=Paths.get(currentDirName);
//        //noinspection resource
//        List<Path> files = Files.list(dir)
//                .filter(file -> Files.isRegularFile(file) && file.toString().contains(metricMD5))
//                .collect(Collectors.toList());
//
//        dir.dir

        List<File> files = Stream.of(Objects.requireNonNull(new File(currentDirName).listFiles()))
                .filter(file -> !file.isDirectory() && file.getName().contains(metricMD5))
                .collect(Collectors.toList());

        for (File file : files) {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));

            while (dis.available() > 0) {
                long timestamp = dis.readLong();
                double value = dis.readDouble();
                DataElement dataElement = new DataElement();

                dataElement.setTimestamp(timestamp);
                dataElement.setValue(value);

                consumer.accept(dataElement);
            }
            dis.close();
        }

        log.debug("FileDriver::readMetric Metric " + metricName + " was read.");
    }

    public List<File> getAllFiles() throws IOException {
        //noinspection resource
//        Files.walk(Paths.get(dirName))
//                .filter(Files::isRegularFile)
//                .filter(path -> path.getFileName().toString().matches(".*_[0-9]+_[0-9]+_[0-9]+"))
//                .collect(Collectors.toList());
//        return

        return getFilesRecursive(new File(dirName));

    }

    private List<File> getFilesRecursive(File file) {
        List<File> res = new ArrayList<>();
        if (file.isDirectory()) {
            for (File file1 : Objects.requireNonNull(file.listFiles())) {
                res.addAll(getFilesRecursive(file1));
            }
        } else {
            res.add(file);
        }
        return res;
    }

    public void removeFile(File path) throws IOException {
        if (!path.delete()) {
            throw new IOException("File for remove not found");
        }
    }
}
