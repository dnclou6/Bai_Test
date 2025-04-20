package com.example.baiTest.repository;

import com.example.baiTest.entity.MajorFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MajorFacilityRepository extends JpaRepository<MajorFacility, UUID> {
    @Query("SELECT mf FROM MajorFacility mf WHERE mf.departmentFacility.facilityId = :facilityId")
    List<MajorFacility> findByFacilityId(UUID facilityId);
    List<MajorFacility> findByDepartmentFacilityId(UUID departmentFacilityId);
    Optional<MajorFacility> findByDepartmentFacilityIdAndMajorName(UUID departmentFacilityId, String majorName);
}

