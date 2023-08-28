package ch.epfl.javelo.projection;

import ch.epfl.javelo.Functions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FunctionsTest {
    @Test
    void checkConstant(){
        var exceptedValue = 1;
        assertEquals(exceptedValue, Functions.constant(1).applyAsDouble(2));

    }

    @Test
    void checkInterpolate(){
        var exceptedValue = 3;
        float testSample[] = {1,2,3,4,5,6,7,8};
        double MaxValue = 8;

        assertEquals(exceptedValue,Functions.sampled(testSample,MaxValue).applyAsDouble(2));

    }

    @Test
    void negatifValue(){
        var exceptedValue = 1;
        float testSample[] = {1,2,3,4,5,6,7,8};
        double MaxValue = 8;

        assertEquals(exceptedValue,Functions.sampled(testSample,MaxValue).applyAsDouble(-1));

    }
    @Test
    void atTheEdgeTest(){
        var exceptedValue = 8;
        float testSample[] = {1,2,3,4,5,6,7,8};
        double MaxValue = 8;

        assertEquals(exceptedValue,Functions.sampled(testSample,MaxValue).applyAsDouble(9));

    }
}
