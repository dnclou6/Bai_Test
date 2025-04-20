package com.example.baiTest.repository;

import com.example.baiTest.entity.ImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImportHistoryRepository extends JpaRepository<ImportHistory, UUID> {
}
