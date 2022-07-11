package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConstantDoubleResultTest {
    @Test
    void getJSON() {
        ConstantDoubleResult constantDoubleResult=new ConstantDoubleResult(10.2);
        System.out.println(constantDoubleResult.getJSON());
    }

    @Test
    void getNewInstanceByJSON() {
        ConstantDoubleResult constantDoubleResult=new ConstantDoubleResult();
        constantDoubleResult.initialize("{\"value\":10.323123}");
        Assertions.assertEquals(10.323123, constantDoubleResult.getValue());
    }
}