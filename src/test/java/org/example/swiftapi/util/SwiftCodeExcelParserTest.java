package org.example.swiftapi.util;

import org.example.swiftapi.model.SwiftCode;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SwiftCodeExcelParserTest {

    private final SwiftCodeExcelParser parser = new SwiftCodeExcelParser();

    @Test
    void shouldParseExcelFileCorrectly() throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("test_swift_codes.xlsx");
        List<SwiftCode> codes = new SwiftCodeExcelParser().parse(stream);

        assertEquals(2, codes.size());

        SwiftCode hq = codes.get(0);
        assertEquals("TESTBANKPLXXX", hq.getSwiftCode());
        assertEquals("Test Bank Polska", hq.getBankName());
        assertEquals("POLAND", hq.getCountryName());
        assertTrue(hq.isHeadquarter());

        SwiftCode branch = codes.get(1);
        assertEquals("TESTBANKPLXX", branch.getSwiftCode());
        assertFalse(branch.isHeadquarter());
        assertEquals("TESTBANKXXX", branch.getHeadquarterCode());
    }
}
