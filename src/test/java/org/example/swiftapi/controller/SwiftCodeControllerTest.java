package org.example.swiftapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.swiftapi.model.SwiftCode;
import org.example.swiftapi.service.SwiftCodeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;


import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SwiftCodeController.class)
class SwiftCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SwiftCodeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnSwiftCodeById() throws Exception {
        SwiftCode code = new SwiftCode();
        code.setSwiftCode("ABCDPLP1XXX");
        code.setBankName("Test Bank");
        code.setCountryISO2("PL");

        ResponseEntity<Object> response = ResponseEntity.ok(code);

        Mockito.<ResponseEntity<?>>when(service.getBySwiftCode("ABCDPLP1XXX"))
                .thenReturn(response);

        mockMvc.perform(get("/v1/swift-codes/ABCDPLP1XXX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swiftCode").value("ABCDPLP1XXX"))
                .andExpect(jsonPath("$.bankName").value("Test Bank"));
    }




    @Test
    void shouldAddNewSwiftCode() throws Exception {
        SwiftCode code = new SwiftCode();
        code.setSwiftCode("NEWTSTPLXXX");
        code.setBankName("Nowy Bank");
        code.setCountryISO2("PL");
        code.setCountryName("POLAND");

        Mockito.doNothing().when(service).saveSingle(any(SwiftCode.class));

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(code)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SWIFT code added successfully"));
    }

    @Test
    void shouldReturnByCountry() throws Exception {
        when(service.getByCountry("PL"))
                .thenReturn(Map.of("countryISO2", "PL", "swiftCodes", List.of()));

        mockMvc.perform(get("/v1/swift-codes/country/PL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryISO2").value("PL"));
    }

    @Test
    void shouldDeleteSwiftCode() throws Exception {
        when(service.delete("ABCDPLP1XXX")).thenReturn(true);

        mockMvc.perform(delete("/v1/swift-codes/ABCDPLP1XXX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SWIFT code deleted successfully"));
    }

    @Test
    void shouldReturnNotFoundWhenSwiftCodeMissing() throws Exception {
        // Zakładamy, że nie ma takiego kodu w bazie
        Mockito.when(service.getBySwiftCode("NOTFOUND123"))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/v1/swift-codes/NOTFOUND123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEmptyListWhenNoCodesForCountry() throws Exception {
        Mockito.when(service.getByCountry("XX"))
                .thenReturn(Map.of(
                        "countryISO2", "XX",
                        "countryName", "",
                        "swiftCodes", List.of()
                ));

        mockMvc.perform(get("/v1/swift-codes/country/XX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryISO2").value("XX"))
                .andExpect(jsonPath("$.swiftCodes").isArray())
                .andExpect(jsonPath("$.swiftCodes").isEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenMissingSwiftCode() throws Exception {
        SwiftCode invalidCode = new SwiftCode();
        invalidCode.setBankName("Invalid Bank");
        invalidCode.setCountryISO2("PL");
        invalidCode.setCountryName("Poland");
        // brak setSwiftCode()

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCode)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingSwiftCode() throws Exception {
        Mockito.when(service.delete("NONEXISTENT")).thenReturn(false);

        mockMvc.perform(delete("/v1/swift-codes/NONEXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("SWIFT code not found"));
    }

    @Test
    void shouldUploadExcelFileSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "swiftcodes.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "dummy content".getBytes() // zawartość nieistotna, symulujemy upload
        );

        Mockito.doNothing().when(service).saveFromExcel(any());

        mockMvc.perform(multipart("/v1/swift-codes/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Excel file uploaded and saved successfully"));
    }


    @Test
    void shouldReturnBadRequestWhenFileIsMissing() throws Exception {
        mockMvc.perform(multipart("/v1/swift-codes/upload"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnServerErrorWhenExcelParsingFails() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "not a real excel file".getBytes()
        );

        // Zamiast robić doNothing, tutaj możemy zasymulować błąd
        Mockito.doThrow(new RuntimeException("Parsing error")).when(service).saveFromExcel(any());

        mockMvc.perform(multipart("/v1/swift-codes/upload").file(file))
                .andExpect(status().is5xxServerError());
    }




}
