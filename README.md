# Bank SWIFT Code API

A simple RESTful API for managing and querying SWIFT bank codes.  
Built with **Spring Boot**, **PostgreSQL**, and **Apache POI** for Excel file parsing.

## Features

- Upload SWIFT codes from `.xlsx` file
- Get SWIFT code details (with branches if headquarter)
- Query all SWIFT codes by country
- Add a single SWIFT code manually
- Delete SWIFT code by code

## Tech Stack

- Java 17
- Spring Boot 3
- PostgreSQL
- Apache POI
- Maven
- JPA (Hibernate)

## Endpoints

### 1. Upload file  
`POST /v1/swift-codes/upload`  
Upload `.xlsx` file containing SWIFT codes.

### 2. Get by SWIFT code  
`GET /v1/swift-codes/{swiftCode}`  
Returns SWIFT code info. If HQ → includes list of branches.

### 3. Get all by country  
`GET /v1/swift-codes/country/{countryISO2}`  
Returns all SWIFT codes for a given ISO2 code (e.g., `PL`, `DE`).

### 4. Add manually  
`POST /v1/swift-codes`  
Add a new SWIFT code using JSON payload.

### 5. Delete  
`DELETE /v1/swift-codes/{swiftCode}`  
Delete SWIFT code by ID.

## Example JSON (POST /v1/swift-codes)

```json
{
  "swiftCode": "DEUTDEFF500",
  "bankName": "Deutsche Bank",
  "address": "Taunusanlage 12",
  "countryISO2": "DE",
  "countryName": "Germany",
  "headquarter": false,
  "headquarterCode": "DEUTDEFFXXX"
}
