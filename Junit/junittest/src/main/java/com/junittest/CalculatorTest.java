package com.junittest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Hello world!
 *
 */
public class CalculatorTest {

    @Test
    void testAdd() {
        Calculator calculator = new Calculator();
        int result = calculator.add(10, 5);
        assertEquals(15, result);
    }
    
    @Test
    void testMultiply() {
        Calculator calculator = new Calculator();
        int result = calculator.multiply(10, 5);
        assertEquals(50, result);
    }
    
    @Test
    void testSub() {
        Calculator calculator = new Calculator();
        int result = calculator.sub(10, 5);
        assertEquals(20, result);
    }
    
    @Test
    void testDiv() {
        Calculator calculator = new Calculator();
        int result = calculator.divide(10, 5);
        assertEquals(20, result);
    }
}