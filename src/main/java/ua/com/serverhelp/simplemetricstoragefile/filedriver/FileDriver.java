package ua.com.serverhelp.simplemetricstoragefile.filedriver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ua.com.serverhelp.simplemetricstoragefile.entities.Event;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

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
        String fileName = dirName + "/" + metricMD5 + "_" + getPeriod();

        File file = new File(fileName);
        boolean fileExists=!file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file,true);
        ObjectOutputStream oos = new ObjectOutputStream(fos){
            @Override
            protected void writeStreamHeader() throws IOException {
                if(fileExists) return;
                super.writeStreamHeader();
            }
        };

        for (DataElement dataElement:dataElements) {
            // write object to file
            oos.writeObject(dataElement);
        }
        oos.close();
    }
    /* public void readFile() {
        try {
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            int i=0;
            try {
                while (true) {
                    Object obj=ois.readObject();
                    i++;
                    System.out.println(obj);
                }
            }catch (EOFException ignored){ }
            System.out.println("i="+i);
            ois.close();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }*/
}
