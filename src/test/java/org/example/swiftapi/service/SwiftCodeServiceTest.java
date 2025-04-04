package org.example.swiftapi.service;

import org.example.swiftapi.model.SwiftCode;
import org.example.swiftapi.repository.SwiftCodeRepository;
import org.example.swiftapi.util.SwiftCodeExcelParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SwiftCodeServiceTest {

    private SwiftCodeRepository repo;
    private SwiftCodeExcelParser parser;
    private SwiftCodeService service;

    @BeforeEach
    void setUp() {
        repo = mock(SwiftCodeRepository.class);
        parser = mock(SwiftCodeExcelParser.class);
        service = new SwiftCodeService(repo, parser);
    }

    @Test
    void shouldReturnSwiftCodeWhenExists() {
        // given
        SwiftCode code = new SwiftCode();
        code.setSwiftCode("ABCDEFXX");
        code.setHeadquarter(false);

        when(repo.findById("ABCDEFXX")).thenReturn(Optional.of(code));

        // when
        var result = service.getBySwiftCode("ABCDEFXX");

        // then
        assertEquals(200, result.getStatusCode().value());
        assertEquals(code, result.getBody());
    }

    @Test
    void shouldReturnNotFoundWhenSwiftCodeMissing() {
        // given
        when(repo.findById("NOTFOUND")).thenReturn(Optional.empty());

        // when
        var result = service.getBySwiftCode("NOTFOUND");

        // then
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void shouldReturnCorrectCountryData() {
        // given
        SwiftCode swift = new SwiftCode();
        swift.setCountryISO2("PL");
        swift.setCountryName("POLAND");

        when(repo.findByCountryISO2("PL")).thenReturn(List.of(swift));

        // when
        Map<String, Object> result = service.getByCountry("pl");

        // then
        assertEquals("PL", result.get("countryISO2"));
        assertEquals("POLAND", result.get("countryName"));
        assertEquals(1, ((List<?>) result.get("swiftCodes")).size());
    }
}
