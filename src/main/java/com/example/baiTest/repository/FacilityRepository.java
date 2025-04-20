package com.example.baiTest.repository;
import com.example.baiTest.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FacilityRepository extends JpaRepository<Facility, UUID> {
}


