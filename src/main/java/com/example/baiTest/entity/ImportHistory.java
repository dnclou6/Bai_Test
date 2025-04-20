package com.example.baiTest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "import_history")
@Data
public class ImportHistory {
    @Id
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(name = "import_date")
    private Long importDate;

    @Column(name = "file_name")
    private String fileName;

    private String status;

    @Column(columnDefinition = "nvarchar(max)")
    private String details;
}
