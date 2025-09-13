package com.example.carins.repo;

import com.example.carins.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, Long> {

    @EntityGraph(attributePaths = {"policy"})
    List<InsuranceClaim> findByCarIdOrderByClaimDateAsc(Long carId);
}
