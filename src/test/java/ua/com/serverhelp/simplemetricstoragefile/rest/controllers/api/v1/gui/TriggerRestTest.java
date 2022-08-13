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

@AutoConfigureMockMvc
@WithMockUser(username = "specuser", authorities = {"GUI"})
class TriggerRestTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getTriggers() throws Exception {
        Trigger trigger = new Trigger();
        trigger.setId(DigestUtils.md5DigestAsHex("Test trigger".getBytes()));
        trigger.setTriggerId("db.test.trigger");
        trigger.setName("Test trigger");
        trigger.setPriority(TriggerPriority.AVERAGE);
        trigger.setConf("");
        trigger.setLastStatus(TriggerStatus.OK);

        triggerRepository.save(trigger);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trigger/")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value("de884fcf16b2f1f9b233307a7f6678f1"));

    }

    @Test
    void getTriggerDetails() throws Exception {
        String id = DigestUtils.md5DigestAsHex("Test trigger".getBytes());
        Trigger trigger = new Trigger();
        trigger.setId(id);
        trigger.setTriggerId("db.test.trigger");
        trigger.setName("Test trigger");
        trigger.setPriority(TriggerPriority.AVERAGE);
        trigger.setConf("");
        trigger.setLastStatus(TriggerStatus.OK);

        triggerRepository.save(trigger);

        Alert alert = new Alert();
        alert.setTrigger(trigger);
        alert.setTriggerStatus(TriggerStatus.OK);
        alertRepository.save(alert);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trigger/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.triggerId").value("db.test.trigger"));

    }
}