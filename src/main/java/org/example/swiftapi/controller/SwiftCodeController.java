package org.example.swiftapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.swiftapi.model.SwiftCode;
import org.example.swiftapi.service.SwiftCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;



import java.util.Map;

@RestController
@RequestMapping("/v1/swift-codes")
@RequiredArgsConstructor
public class SwiftCodeController {

    private final SwiftCodeService service;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        service.saveFromExcel(file);
        return ResponseEntity.ok(Map.of("message", "Excel file uploaded and saved successfully"));
    }



    @GetMapping("/{swiftCode}")
    public ResponseEntity<?> getBySwiftCode(@PathVariable String swiftCode) {
        return service.getBySwiftCode(swiftCode);
    }

    @GetMapping("/country/{countryISO2}")
    public ResponseEntity<Map<String, Object>> getByCountry(@PathVariable String countryISO2) {
        return ResponseEntity.ok(service.getByCountry(countryISO2));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addSwiftCode(@RequestBody @Valid SwiftCode code) {
        service.saveSingle(code);
        return ResponseEntity.ok(Map.of("message", "SWIFT code added successfully"));
    }

    @DeleteMapping("/{swiftCode}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String swiftCode) {
        boolean deleted = service.delete(swiftCode);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "SWIFT code not found"));
        }
        return ResponseEntity.ok(Map.of("message", "SWIFT code deleted successfully"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
}
