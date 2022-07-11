package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConstantDoubleExpressionTest {
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