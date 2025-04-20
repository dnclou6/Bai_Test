package com.example.baiTest.repository;

import com.example.baiTest.entity.StaffMajorFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface StaffMajorFacilityRepository extends JpaRepository<StaffMajorFacility, UUID> {
    @Query("SELECT smf FROM StaffMajorFacility smf " +
            "WHERE smf.staffId = :staffId AND smf.majorFacility.departmentFacility.facilityId = :facilityId")
    Optional<StaffMajorFacility> findByStaffIdAndFacilityId(UUID staffId, UUID facilityId);

    List<StaffMajorFacility> findByStaffId(UUID staffId);
}
