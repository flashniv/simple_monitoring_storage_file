package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
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

import java.util.List;

@AutoConfigureMockMvc
class TriggerRestTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "specuser", authorities = {"GUI"})
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
    @WithMockUser(username = "org1user", authorities = {"GUI"})
    void getTriggersCheckPermissions() throws Exception {
        Trigger trigger = new Trigger();
        trigger.setId(DigestUtils.md5DigestAsHex("Test trigger org1".getBytes()));
        trigger.setTriggerId("db.organization1.trigger");
        trigger.setName("Test trigger");
        trigger.setPriority(TriggerPriority.AVERAGE);
        trigger.setConf("");
        trigger.setLastStatus(TriggerStatus.OK);
        triggerRepository.save(trigger);

        Trigger trigger1 = new Trigger();
        trigger1.setId(DigestUtils.md5DigestAsHex("Test trigger".getBytes()));
        trigger1.setTriggerId("db.test.trigger");
        trigger1.setName("Test trigger");
        trigger1.setPriority(TriggerPriority.AVERAGE);
        trigger1.setConf("");
        trigger1.setLastStatus(TriggerStatus.OK);
        triggerRepository.save(trigger1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trigger/")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value("ed3244db21d85d2f4da34bd575d1b742"));

    }

    @Test
    @WithMockUser(username = "specuser", authorities = {"GUI"})
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

    @Test
    @WithMockUser(username = "specuser", authorities = {"GUI"})
    void deleteAll() throws Exception {
        Trigger trigger = new Trigger();
        trigger.setId(DigestUtils.md5DigestAsHex("Test trigger org1".getBytes()));
        trigger.setTriggerId("db.organization1.trigger");
        trigger.setName("Test trigger");
        trigger.setPriority(TriggerPriority.AVERAGE);
        trigger.setConf("");
        trigger.setLastStatus(TriggerStatus.OK);
        triggerRepository.save(trigger);

        Trigger trigger1 = new Trigger();
        trigger1.setId(DigestUtils.md5DigestAsHex("Test trigger".getBytes()));
        trigger1.setTriggerId("db.test.trigger");
        trigger1.setName("Test trigger");
        trigger1.setPriority(TriggerPriority.AVERAGE);
        trigger1.setConf("");
        trigger1.setLastStatus(TriggerStatus.OK);
        triggerRepository.save(trigger1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/trigger/deleteAll")
                        .content(new JSONArray(List.of(trigger.getId(),trigger1.getId())).toString())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
        Assertions.assertTrue(triggerRepository.findAll().isEmpty());

    }

    @Test
    @WithMockUser(username = "specuser", authorities = {"GUI"})
    void suppressAll() throws Exception{
        Trigger trigger = new Trigger();
        trigger.setId(DigestUtils.md5DigestAsHex("Test trigger org1".getBytes()));
        trigger.setTriggerId("db.organization1.trigger");
        trigger.setName("Test trigger");
        trigger.setPriority(TriggerPriority.AVERAGE);
        trigger.setConf("");
        trigger.setLastStatus(TriggerStatus.OK);
        triggerRepository.save(trigger);

        Trigger trigger1 = new Trigger();
        trigger1.setId(DigestUtils.md5DigestAsHex("Test trigger".getBytes()));
        trigger1.setTriggerId("db.test.trigger");
        trigger1.setName("Test trigger");
        trigger1.setPriority(TriggerPriority.AVERAGE);
        trigger1.setConf("");
        trigger1.setLastStatus(TriggerStatus.OK);
        triggerRepository.save(trigger1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/trigger/suppressAll")
                        .param("suppress", "true")
                        .content(new JSONArray(List.of(trigger.getId(),trigger1.getId())).toString())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
        List<Trigger> triggerList = triggerRepository.findAll();
        Assertions.assertEquals(2, triggerList.size());
        Assertions.assertEquals(true, triggerList.get(0).getSuppressed());
        Assertions.assertEquals(true, triggerList.get(1).getSuppressed());
    }

    @Test
    @WithMockUser(username = "specuser", authorities = {"GUI"})
    void enableAll() throws Exception{
        Trigger trigger = new Trigger();
        trigger.setId(DigestUtils.md5DigestAsHex("Test trigger org1".getBytes()));
        trigger.setTriggerId("db.organization1.trigger");
        trigger.setName("Test trigger");
        trigger.setPriority(TriggerPriority.AVERAGE);
        trigger.setConf("");
        trigger.setLastStatus(TriggerStatus.OK);
        triggerRepository.save(trigger);

        Trigger trigger1 = new Trigger();
        trigger1.setId(DigestUtils.md5DigestAsHex("Test trigger".getBytes()));
        trigger1.setTriggerId("db.test.trigger");
        trigger1.setName("Test trigger");
        trigger1.setPriority(TriggerPriority.AVERAGE);
        trigger1.setConf("");
        trigger1.setLastStatus(TriggerStatus.OK);
        triggerRepository.save(trigger1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/trigger/enableAll")
                        .param("enable", "false")
                        .content(new JSONArray(List.of(trigger.getId(),trigger1.getId())).toString())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
        List<Trigger> triggerList = triggerRepository.findAll();
        Assertions.assertEquals(2, triggerList.size());
        Assertions.assertEquals(false, triggerList.get(0).getEnabled());
        Assertions.assertEquals(false, triggerList.get(1).getEnabled());
    }
}