package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.DigestUtils;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerPriority;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerStatus;

import java.time.Instant;

@AutoConfigureMockMvc
class AlertRestTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "specuser", authorities = {"GUI"})
    void getAlerts() throws Exception {
        Trigger trigger = new Trigger();
        trigger.setId(DigestUtils.md5DigestAsHex("Test trigger".getBytes()));
        trigger.setTriggerId("db.test.trigger");
        trigger.setName("Test trigger");
        trigger.setPriority(TriggerPriority.AVERAGE);
        trigger.setConf("");
        trigger.setLastStatus(TriggerStatus.OK);

        triggerRepository.save(trigger);

        Alert alert = new Alert();
        alert.setTrigger(trigger);
        alert.setOperationData("");
        alert.setAlertTimestamp(Instant.now());
        alert.setTriggerStatus(TriggerStatus.UNCHECKED);

        alertRepository.save(alert);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/alert/")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].trigger.id").value("de884fcf16b2f1f9b233307a7f6678f1"));
    }

    @Test
    @WithMockUser(username = "org1user", authorities = {"GUI"})
    void getAlertsCheckPermissions() throws Exception {
        Trigger trigger = new Trigger();
        trigger.setId(DigestUtils.md5DigestAsHex("Test trigger".getBytes()));
        trigger.setTriggerId("db.test.trigger");
        trigger.setName("Test trigger");
        trigger.setPriority(TriggerPriority.AVERAGE);
        trigger.setConf("");
        trigger.setLastStatus(TriggerStatus.OK);

        triggerRepository.save(trigger);

        Alert alert = new Alert();
        alert.setTrigger(trigger);
        alert.setOperationData("");
        alert.setAlertTimestamp(Instant.now());
        alert.setTriggerStatus(TriggerStatus.UNCHECKED);

        alertRepository.save(alert);

        Trigger trigger1 = new Trigger();
        trigger1.setId(DigestUtils.md5DigestAsHex("Test trigger org1".getBytes()));
        trigger1.setTriggerId("db.organization1.trigger");
        trigger1.setName("Test trigger");
        trigger1.setPriority(TriggerPriority.AVERAGE);
        trigger1.setConf("");
        trigger1.setLastStatus(TriggerStatus.OK);

        triggerRepository.save(trigger1);

        Alert alert1 = new Alert();
        alert1.setTrigger(trigger1);
        alert1.setOperationData("");
        alert1.setAlertTimestamp(Instant.now());
        alert1.setTriggerStatus(TriggerStatus.UNCHECKED);

        alertRepository.save(alert1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/alert/")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].trigger.id").value("ed3244db21d85d2f4da34bd575d1b742"));
    }
}