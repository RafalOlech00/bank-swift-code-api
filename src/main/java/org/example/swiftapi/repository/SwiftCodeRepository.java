package org.example.swiftapi.repository;

import org.example.swiftapi.model.SwiftCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SwiftCodeRepository extends JpaRepository<SwiftCode, String> {
    List<SwiftCode> findByCountryISO2(String countryISO2);
    List<SwiftCode> findByHeadquarterCode(String headquarterCode);
}
