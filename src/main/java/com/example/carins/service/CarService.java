package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsuranceClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.*;

import org.springframework.stereotype.Service;
import com.example.carins.web.error.NotFoundException;

import java.time.LocalDate;
import java.util.*;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final InsuranceClaimRepository claimRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository, InsuranceClaimRepository claimRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null || date == null) return false;
        if (!carRepository.existsById(carId)) {
            throw new NotFoundException("Car doesn't exist!");
        }
        return policyRepository.existsActiveOnDate(carId, date);
    }

    public boolean carExists(Long id) {
        return carRepository.existsById(id);
    }
    
    public InsuranceClaim createClaim(Long carId, LocalDate claimDate, String description, Double amount) {
        var car = carRepository.findById(carId).orElse(null);
        if (car == null) return null;
        var activePolicy = policyRepository.findActiveOnDate(carId, claimDate).stream().findFirst().orElse(null);
        if (activePolicy == null) return null;
        var claim = new InsuranceClaim(car, activePolicy, claimDate, description, amount);
        return claimRepository.save(claim);
    }

    public List<InsuranceClaim> getCarHistory(Long carId) {
        if (carId == null) return null;
        if (!carExists(carId)) return null;
        return claimRepository.findByCarIdOrderByClaimDateAsc(carId);
    }
}
