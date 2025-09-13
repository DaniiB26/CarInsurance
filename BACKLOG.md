# Tasks

## Task A
- Added `@NotNull` and `@Column(nullable = false)` to `startDate` and `endDate` in `InsurancePolicy`.
- Updated `import.sql` to include `endDate`.

## Task B
### 1) Register Insurance Claim
- Created `InsuranceClaim` entity with fields: car, policy, claimDate, description, amount.
- Added `InsuranceClaimRepository` and `findByCarIdOrderByClaimDateAsc` with `@EntityGraph(attributePaths = {"policy"})`.
- Service: `createClaim(carId, date, description, amount)` attaches the active policy for that date; returns `null` if the car doesn’t exist or no active policy matches the date; otherwise saves and returns the claim.
- Added `ClaimDto` with validation: `@NotNull claimDate`, `@NotBlank description`, `@NotNull @Positive amount`.
- Controller: `POST /api/cars/{carId}/claims` accepts validated body, returns `201 Created` with `Location` and the created claim; returns `404` for missing car or no active policy; returns `400` for validation errors.

### 2) Show the History of a Car
- I added the policy to the claim because when a claim is made i think the policy is necessary, and also it worked out for the history of cars
- Service: `getCarHistory(carId)` returns all claims for the car ordered by `claimDate` (or `null` if the car does not exist).
- Controller: `GET /api/cars/{carId}/history` returns `200 OK` with a list of `CarHistoryDto` (car + policy + claim details) or `404` when the car is missing.
- Added `CarHistoryDto` including VIN, make, model, year, policy provider and start/end, and claim date/description/amount.


## Task C


