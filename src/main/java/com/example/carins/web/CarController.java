package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.CarHistoryDto;
import com.example.carins.web.dto.ClaimDto;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId, @RequestParam String date) {
        try {
            LocalDate d = LocalDate.parse(date);
            var min = LocalDate.of(1900, 1, 1);
            var max = LocalDate.of(2100, 12, 31);
            if (d.isBefore(min) || d.isAfter(max)) {
                return ResponseEntity.badRequest().body("Date must be between " + min + " and " + max);
            }
            if (!service.carExists(carId)) {
                return ResponseEntity.notFound().build();
            }
            boolean valid = service.isInsuranceValid(carId, d);
            return ResponseEntity.ok(new InsuranceValidityResponse(carId, d.toString(), valid));

        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().body("Date format must be YYYY-MM-DD");
        }
    }

    @GetMapping("/cars/{carId}/history")
    public ResponseEntity<?> getCarHistory(@PathVariable Long carId) {
        if (carId == null) {
            return ResponseEntity.badRequest().body("Car ID must be provided");
        }

        if (!service.carExists(carId)) {
            return ResponseEntity.notFound().build();
        }

        var claims = service.getCarHistory(carId);
        if (claims == null) {
            return ResponseEntity.notFound().build();
        }
        var dtoList = claims.stream().map(this::toHistoryDto).toList();
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/cars/{carId}/claims")
    public ResponseEntity<?> createClaim(@PathVariable Long carId, @Valid @RequestBody ClaimDto body) {
        if (carId == null) {
            return ResponseEntity.badRequest().body("Car ID must be provided");
        }
        var created = service.createClaim(carId, body.claimDate(), body.description(), body.amount());
        if (created == null) {
            return ResponseEntity.notFound().build();
        }
        var location = java.net.URI.create("/api/cars/" + carId + "/claims/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    private CarDto toDto(Car c) {
        var o = c.getOwner();
        return new CarDto(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }

    private CarHistoryDto toHistoryDto(InsuranceClaim claim) {
        var policy = claim.getPolicy();
        var car = policy.getCar();
        return new CarHistoryDto(
                car.getId(),
                car.getVin(),
                car.getMake(),
                car.getModel(),
                car.getYearOfManufacture(),
                policy.getProvider(),
                policy.getStartDate(),
                policy.getEndDate(),
                claim.getClaimDate(),
                claim.getDescription(),
                claim.getAmount()
        );
    }

    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {}
}
