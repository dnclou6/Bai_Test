package com.example.baiTest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "major_facility")
@Data
public class MajorFacility {
    @Id
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    private Byte status;
    @Column(name = "created_date")
    private Long createdDate;
    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    @Column(name = "id_department_facility", columnDefinition = "uniqueidentifier")
    private UUID departmentFacilityId;

    @Column(name = "id_major", columnDefinition = "uniqueidentifier")
    private UUID majorId;

    @ManyToOne
    @JoinColumn(name = "id_department_facility", insertable = false, updatable = false)
    private DepartmentFacility departmentFacility;

    @ManyToOne
    @JoinColumn(name = "id_major", insertable = false, updatable = false)
    private Major major;
}
