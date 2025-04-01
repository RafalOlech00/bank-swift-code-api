package org.example.swiftapi.service;

import lombok.RequiredArgsConstructor;
import org.example.swiftapi.model.SwiftCode;
import org.example.swiftapi.repository.SwiftCodeRepository;
import org.example.swiftapi.util.SwiftCodeExcelParser;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SwiftCodeService {

    private final SwiftCodeRepository repo;
    private final SwiftCodeExcelParser parser;

    public ResponseEntity<?> getBySwiftCode(String swift) {
        Optional<SwiftCode> codeOpt = repo.findById(swift);
        if (codeOpt.isEmpty()) return ResponseEntity.notFound().build();

        SwiftCode code = codeOpt.get();
        if (code.isHeadquarter()) {
            List<SwiftCode> branches = repo.findByHeadquarterCode(swift).stream()
                    .filter(b -> !b.getSwiftCode().equals(swift))
                    .toList();

            Map<String, Object> result = new LinkedHashMap<>(Map.of(
                    "address", code.getAddress(),
                    "bankName", code.getBankName(),
                    "countryISO2", code.getCountryISO2(),
                    "countryName", code.getCountryName(),
                    "isHeadquarter", true,
                    "swiftCode", code.getSwiftCode()
            ));
            result.put("branches", branches);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.ok(code);
        }
    }

    public Map<String, Object> getByCountry(String iso2) {
        String upperISO2 = iso2.toUpperCase();
        List<SwiftCode> codes = repo.findByCountryISO2(upperISO2);
        String countryName = codes.stream().findFirst()
                .map(SwiftCode::getCountryName).orElse("");

        return Map.of(
                "countryISO2", upperISO2,
                "countryName", countryName,
                "swiftCodes", codes
        );
    }

    public void saveSingle(SwiftCode code) {
        code.setCountryISO2(code.getCountryISO2().toUpperCase());
        code.setCountryName(code.getCountryName().toUpperCase());
        if (!code.isHeadquarter()) {
            code.setHeadquarterCode(code.getSwiftCode().substring(0, 8) + "XXX");
        }
        repo.save(code);
    }

    public boolean delete(String swiftCode) {
        if (repo.existsById(swiftCode)) {
            repo.deleteById(swiftCode);
            return true;
        }
        return false;
    }

    public List<SwiftCode> getAll() {
        return repo.findAll();
    }

    public void saveFromExcel(MultipartFile file) {
        try {
            List<SwiftCode> codes = parser.parse(file.getInputStream());
            repo.saveAll(codes);
        } catch (Exception e) {
            e.printStackTrace();
            // Możesz też zwrócić ResponseEntity z błędem jeśli chcesz
        }
    }

}
