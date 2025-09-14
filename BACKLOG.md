# Tasks

## Task A
- Added `@NotNull` and `@Column(nullable = false)` to `startDate` and `endDate` in `InsurancePolicy`.
- Updated `import.sql` to include `endDate`.

## Task B
### 1) Register Insurance Claim
- Created `InsuranceClaim` entity with fields: car, policy, claimDate, description, amount.
- Added `InsuranceClaimRepository` and `findByCarIdOrderByClaimDateAsc` with `@EntityGraph(attributePaths = {"policy, car"})`.
- Service: `createClaim(carId, date, description, amount)` attaches the active policy for that date; returns `null` if the car doesn’t exist or no active policy matches the date; otherwise saves and returns the claim.
- Added `ClaimDto` with validation: `@NotNull claimDate`, `@NotBlank description`, `@NotNull @Positive amount`.
- Controller: `POST /api/cars/{carId}/claims` accepts validated body, returns `201 Created` with `Location` and the created claim; returns `404` for missing car or no active policy; returns `400` for validation errors.

### 2) Show the History of a Car
- I added the policy to the claim because when a claim is made i think the policy is necessary, and also it worked out for the history of cars
- Service: `getCarHistory(carId)` returns all claims for the car ordered by `claimDate` (or `null` if the car does not exist).
- Controller: `GET /api/cars/{carId}/history` returns `200 OK` with a list of `CarHistoryDto` (car + policy + claim details) or `404` when the car is missing.
- Added `CarHistoryDto` including VIN, make, model, year, policy provider and start/end, and claim date/description/amount.

### Sample requests - Used Postman

Register an insurance claim (201 Created):
```bash
POST http://localhost:8080/api/cars/1/claims
Content-Type: application/json

{
  "claimDate": "2025-06-15",
  "description": "Rear bumper damage",
  "amount": 1250.00
}
```

Request JSON (ClaimDto):
```json
{
  "claimDate": "YYYY-MM-DD",
  "description": "string (required)",
  "amount": 123.45
}
```

Successful response JSON (entity):
```json
{
  "id": 1,
  "policy": {
    "id": 2,
    "provider": "Groupama",
    "startDate": "2025-01-01",
    "endDate": "2025-12-31"
  },
  "claimDate": "2025-06-15",
  "description": "Rear bumper damage",
  "amount": 1250.0
}
```

Notes:
- 404 when `carId` does not exist or there is no active policy on `claimDate`.
- 400 on validation errors (`claimDate` required, `description` non-blank, `amount` positive).

Get car history (claims) for a car (200 OK):
```bash
GET http://localhost:8080/api/cars/1/history
```

Response JSON (list of CarHistoryDto in chronological order):
```json
[
  {
    "carId": 1,
    "vin": "VIN12345",
    "make": "Dacia",
    "model": "Logan",
    "year": 2018,
    "policyProvider": "Allianz",
    "policyStartDate": "2024-01-01",
    "policyEndDate": "2024-12-31",
    "claimDate": "2024-07-10",
    "claimDescription": "Windshield replacement",
    "claimAmount": 300.0
  },
  {
    "carId": 1,
    "vin": "VIN12345",
    "make": "Dacia",
    "model": "Logan",
    "year": 2018,
    "policyProvider": "Groupama",
    "policyStartDate": "2025-01-01",
    "policyEndDate": "2025-12-31",
    "claimDate": "2025-06-15",
    "claimDescription": "Rear bumper damage",
    "claimAmount": 1250.0
  }
]
```

## Task C

I didn’t have much experience with MVC tests before, but after reading a bit, I realized they are actually very straightforward and easy to implement.

### What it validates:
- **Date format** must be ISO (`YYYY-MM-DD`).  
  -> If not, returns **400** with message: *"Date format must be YYYY-MM-DD"*.

- **Date range** must be between `1900-01-01` and `2100-12-31`.  
  -> If not, returns **400** with message showing the allowed range.

- **Car existence** is verified with `service.carExists(carId)`.  
  -> If the car does not exist, returns **404**.

- **Success case**: returns **200 OK** with JSON

### Tests (WebMvcTest)

- Invalid format -> **400** with correct message.

- Out of range -> **400** with correct message.

- Non-existing car -> **404**.

- Succes -> **200 OK**, JSON fields correct.

I also added carExists(Long carId) in the service to support these checks.

## Task D

- I started this task by enabling scheduling.
- Next, I created a method to return the list of insurance policies by their **end date**.
- Finally, I added a **scheduler** in the utils. My approach was that, once a new day begins, the scheduler runs and checks which policies expired on the previous day.

## Todos

- For the **TODO** from the **Car Repository** I added `@Column(unique = true, nullable = false)` to make sure it is unique and also created the function `Boolean existsByVin(String vin)` to see if there are no duplicates.
- Implemented the second **TODO** from **Service** by introducing a custom `NotFoundException` (extends RuntimeException, annotated with @ResponseStatus(HttpStatus.NOT_FOUND)). `CarService.isInsuranceValid` now throws it when the car ID doesn’t exist, and the controller maps it to 404 automatically.
- Also I added the **Logger** in scheduler 