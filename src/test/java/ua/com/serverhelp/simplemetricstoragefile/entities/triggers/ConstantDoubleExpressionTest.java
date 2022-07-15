package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ExpressionException;

class ConstantDoubleExpressionTest extends AbstractTest {
    @Test
    void getJSON() {
        ConstantDoubleExpression constantDoubleExpression = new ConstantDoubleExpression(10.2);
        System.out.println(constantDoubleExpression.getJSON());
    }

    @Test
    void getNewInstanceByJSON() throws ExpressionException {
        ConstantDoubleExpression constantDoubleExpression = new ConstantDoubleExpression();
        constantDoubleExpression.initialize("{\"value\":10.323123}");
        Assertions.assertEquals(10.323123, constantDoubleExpression.getValue());
    }
}