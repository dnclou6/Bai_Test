package com.example.baiTest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "facility")
@Data
public class Facility {
    @Id
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    private Byte status;
    @Column(name = "created_date")
    private Long createdDate;
    @Column(name = "last_modified_date")
    private Long lastModifiedDate;
    private String code;
    private String name;
}
