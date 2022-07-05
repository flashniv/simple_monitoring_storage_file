package ua.com.serverhelp.simplemetricstoragefile.filedriver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileDriver {
    @Value("${metric-storage.metrics-directory}")
    private String dirName;

    private String getPeriod() {
        LocalDate now = LocalDate.now();
        return now.getYear() + "_" + now.getMonthValue() + "_" + now.getDayOfMonth();
    }

    public void writeMetric(String metricName, List<DataElement> dataElements) throws IOException {
        String metricMD5 = DigestUtils.md5DigestAsHex(metricName.getBytes());
        String fileName = dirName + "/"+metricMD5.substring(0, 2)+"/"+metricMD5.substring(2,4)+"/"+ metricMD5 + "_" + getPeriod();

        File file = new File(fileName);
        file.getParentFile().mkdirs();
        boolean fileExists = !file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file, true);
        ObjectOutputStream oos = new ObjectOutputStream(fos) {
            @Override
            protected void writeStreamHeader() throws IOException {
                if (fileExists) return;
                super.writeStreamHeader();
            }
        };

        for (DataElement dataElement : dataElements) {
            // write object to file
            oos.writeObject(dataElement);
        }
        oos.close();
        log.debug("FileDriver::writeMetric Metric "+metricName+" was write. Events="+dataElements.size());
    }

    public List<DataElement> readFile(String metricName) throws IOException, ClassNotFoundException {
        String metricMD5 = DigestUtils.md5DigestAsHex(metricName.getBytes());
        String fileName = dirName + "/"+metricMD5.substring(0, 2)+"/"+metricMD5.substring(2,4)+"/"+ metricMD5 + "_" + getPeriod();
        List<DataElement> dataElements=new ArrayList<>();

        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);

        int i = 0;
        try {
            while (true) {
                Object obj = ois.readObject();
                i++;
                dataElements.add((DataElement) obj);
            }
        } catch (EOFException ignored) {
        }
        log.debug("FileDriver::readFile Metric "+metricName+" was read. "+i+" values");
        ois.close();
        return dataElements;
    }
}
