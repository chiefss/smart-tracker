package org.devel.smarttracker.functional.utils;

import org.devel.smarttracker.functional.AbstractFunctionalTest;
import org.devel.smarttracker.application.utils.CurrencyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class TestCurrencyUtils extends AbstractFunctionalTest {

    @Test
    void testFormatCurrency() {
        String currency = CurrencyUtils.formatCurrency(123456.789f);
        Assertions.assertEquals("123 456,79", currency);
    }

    @Test
    void testGetCurrencySubstring() throws IOException {
        Assertions.assertThrows(IOException.class, () -> CurrencyUtils.getCurrencySubstring("f asdf c"));
        double currency;
        currency = CurrencyUtils.getCurrencySubstring("f 123456 c");
        Assertions.assertEquals(123456, currency);
        currency = CurrencyUtils.getCurrencySubstring(" 123 456 ");
        Assertions.assertEquals(123456, currency);
        currency = CurrencyUtils.getCurrencySubstring("f 123.456 c");
        Assertions.assertEquals(123.456, currency);
        currency = CurrencyUtils.getCurrencySubstring("f 123,456 c");
        Assertions.assertEquals(123.456, currency);
        currency = CurrencyUtils.getCurrencySubstring("f 12 3,45.6 c");
        Assertions.assertEquals(12345.6, currency);
        currency = CurrencyUtils.getCurrencySubstring("f 12,3,45.6 c");
        Assertions.assertEquals(12345.6, currency);
        currency = CurrencyUtils.getCurrencySubstring("f 12.3.45,6 c");
        Assertions.assertEquals(12345.6, currency);
    }

}
