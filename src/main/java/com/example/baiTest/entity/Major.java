package com.example.baiTest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "major")
@Data
public class Major {
    @Id
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    private Byte status;
    @Column(name = "created_date")
    private Long createdDate;
    @Column(name = "last_modified_date")
    private Long lastModifiedDate;
    private String name;
    private String code;
}
