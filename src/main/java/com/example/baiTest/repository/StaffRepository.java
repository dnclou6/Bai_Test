package com.example.baiTest.repository;

import com.example.baiTest.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID>, JpaSpecificationExecutor<Staff> {
    boolean existsByAccountFe(String accountFe);
    boolean existsByAccountFpt(String accountFpt);
    boolean existsByStaffCode(String staffCode);
}