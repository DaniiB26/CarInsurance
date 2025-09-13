package com.example.carins.utils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.carins.repo.InsurancePolicyRepository;

@Component
public class PolicyExpiryScheduler {
    
    private final InsurancePolicyRepository policyRepository;
    private final Set<Long> expiredPolicies = new HashSet<>();

    public PolicyExpiryScheduler(InsurancePolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkExpiringPolicies() {
        var yesterday = LocalDate.now().minusDays(1);

        for (var p : policyRepository.findByEndDate(yesterday)) {
            if (expiredPolicies.add(p.getId())) {
                System.out.println("Policy " + p.getId() + " for car " + p.getCar().getId() + " expired on " + p.getEndDate() + "\n");
            }
        }
    }
}
