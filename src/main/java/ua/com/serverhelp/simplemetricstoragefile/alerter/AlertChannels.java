package ua.com.serverhelp.simplemetricstoragefile.alerter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemetricstoragefile.alerter.sender.AlertSender;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.Expression;
import ua.com.serverhelp.simplemetricstoragefile.storage.AlertChannelRepository;
import ua.com.serverhelp.simplemetricstoragefile.storage.AlertFilterRepository;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
public class AlertChannels {
    @Autowired
    private AlertChannelRepository alertChannelRepository;
    @Autowired
    private AlertFilterRepository alertFilterRepository;

    public void sendAlert(Alert alert) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<AlertChannel> alertChannels=alertChannelRepository.findAll();
        for (AlertChannel alertChannel:alertChannels){
            boolean pass=true;
            for (AlertFilter alertFilter: alertChannel.getAlertFilters()){
                if(alert.getTrigger().getTriggerId().matches(alertFilter.getRegexp())){
                    pass=false;
                    break;
                }
            }
            if (!pass){
                AlertSender alertSender=getAlertSender(alertChannel);
                alertSender.sendMessage(alert);
            }
        }
    }

    private AlertSender getAlertSender(AlertChannel alertChannel) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> arg1Class = Class.forName(alertChannel.getAlerterClass());
        AlertSender alertSender = (AlertSender) arg1Class.getConstructor().newInstance();
        alertSender.initialize(alertChannel.getAlerterParameters());

        return alertSender;
    }
}
