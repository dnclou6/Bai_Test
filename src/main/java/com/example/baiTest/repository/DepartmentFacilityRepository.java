package com.example.baiTest.repository;
import com.example.baiTest.entity.DepartmentFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentFacilityRepository extends JpaRepository<DepartmentFacility, UUID> {
    @Query("SELECT df FROM DepartmentFacility df WHERE df.facilityId = :facilityId")
    List<DepartmentFacility> findByFacilityId(UUID facilityId);
}
