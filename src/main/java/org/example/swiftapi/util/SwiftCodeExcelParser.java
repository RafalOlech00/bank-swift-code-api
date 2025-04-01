package org.example.swiftapi.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.swiftapi.model.SwiftCode;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class SwiftCodeExcelParser {

    public List<SwiftCode> parse(InputStream inputStream) {
        List<SwiftCode> result = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // pomiń nagłówki

                SwiftCode code = new SwiftCode();

                code.setCountryISO2(row.getCell(0).getStringCellValue());      // COUNTRY ISO2 CODE
                code.setSwiftCode(row.getCell(1).getStringCellValue());        // SWIFT CODE
                code.setBankName(row.getCell(3).getStringCellValue());         // NAME
                code.setAddress(row.getCell(4).getStringCellValue());          // ADDRESS
                code.setCountryName(row.getCell(6).getStringCellValue());      // COUNTRY NAME

                code.setHeadquarter(code.getSwiftCode().endsWith("XXX"));
                code.setHeadquarterCode(code.getSwiftCode().substring(0, 8) + "XXX");

                result.add(code);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
