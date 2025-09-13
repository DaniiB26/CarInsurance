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