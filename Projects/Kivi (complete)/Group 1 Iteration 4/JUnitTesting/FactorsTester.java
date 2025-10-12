import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class FactorsTester {

    @Test
    void testPerfect1()
    {    
        // TEST 1: should throw the exception because the parameter value is less than 1
        assertThrows(IllegalArgumentException.class, () -> FactorsUtility.perfect(0));
    }
    
    @Test
    void testPerfect2()
    {    
        // TEST 2: should succeed because 1 is a valid parameter value, but is not a perfect number
        assertFalse(FactorsUtility.perfect(1));
    }
    
    @Test
    void testPerfect3()
    {    
        // TEST 3: should succeed because 6 is a valid parameter value, and is a perfect number
        assertTrue(FactorsUtility.perfect(6));
    }
    
    @Test
    void testPerfect4()
    {    
        // TEST 4: should succeed because 7 is a valid parameter value, but is not a perfect number
        boolean expected = false;
        assertEquals(expected, FactorsUtility.perfect(7));
    }
    

    @Test
    void testGetFactors1() {
        // a > 1
        assertEquals(Arrays.asList(1), FactorsUtility.getFactors(2));
    }

    @Test
    void testGetFactors2() {
        // a = 1
        assertTrue(FactorsUtility.getFactors(1).isEmpty());
    }

    @Test
    void testGetFactors3() {
        // a = 0
        assertTrue(FactorsUtility.getFactors(0).isEmpty());
    }

    @Test
    void testGetFactors4() {
        // a < 0
        assertThrows(IllegalArgumentException.class, () -> FactorsUtility.getFactors(-1));
    }

    @Test
    void testGetFactors5() {
        // value with several factors
        assertEquals(Arrays.asList(1,2,3,4,6), FactorsUtility.getFactors(12));
    }

    @Test
    void testFactor1() {
        // valid factor
        assertTrue(FactorsUtility.factor(10, 5));
    }

    @Test
    void testFactor2() {
        // valid non-factor
        assertFalse(FactorsUtility.factor(10, 3));
    }

    @Test
    void testFactor3() {
        // a < 0
        assertThrows(IllegalArgumentException.class, () -> FactorsUtility.factor(-1, 1));
    }

    @Test
    void testFactor4() {
        // b < 1
        assertThrows(IllegalArgumentException.class, () -> FactorsUtility.factor(10, 0));
    }

    @Test
    void testFactor5() {
        // b = a
        assertTrue(FactorsUtility.factor(2, 2));
    }
}
