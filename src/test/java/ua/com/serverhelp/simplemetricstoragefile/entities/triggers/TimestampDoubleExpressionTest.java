package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;

class TimestampDoubleExpressionTest extends AbstractTest {

    @Test
    void getJSON() {
        TimestampDoubleExpression timestampDoubleExpression = new TimestampDoubleExpression();
        System.out.println(timestampDoubleExpression.getJSON());
    }

    @Test
    void getValue() throws ExpressionException {
        TimestampDoubleExpression timestampDoubleExpression = new TimestampDoubleExpression();
        System.out.println(timestampDoubleExpression.getValue());
    }
}