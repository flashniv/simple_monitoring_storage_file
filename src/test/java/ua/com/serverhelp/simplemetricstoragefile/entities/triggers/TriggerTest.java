package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ExpressionException;

import java.lang.reflect.InvocationTargetException;

class TriggerTest extends AbstractTest {
    private Trigger trigger;

    @BeforeEach
    void setUp2() {
        trigger = new Trigger();
        trigger.setId(DigestUtils.md5DigestAsHex("test.stage.db.booleanitem1{}".getBytes()));
        trigger.setTriggerId("test.stage.db.booleanitem1{}");
        trigger.setName("Test trigger");
        trigger.setConf("{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.CompareDoubleExpression\",\"parameters\":{\"operation\":\"<\",\"arg2\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\",\"parameters\":{\"value\":0.6}},\"arg1\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\",\"parameters\":{\"value\":0.5}}}}");
    }

    @Test
    void checkTrigger() throws ClassNotFoundException, InvocationTargetException, ExpressionException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Assertions.assertTrue(trigger.checkTrigger());
    }
}