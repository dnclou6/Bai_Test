package com.example.baiTest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "department_facility")
@Data
public class DepartmentFacility {
    @Id
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    private Byte status;
    @Column(name = "created_date")
    private Long createdDate;
    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    @Column(name = "id_department", columnDefinition = "uniqueidentifier")
    private UUID departmentId;

    @Column(name = "id_facility", columnDefinition = "uniqueidentifier")
    private UUID facilityId;

    @Column(name = "id_staff", columnDefinition = "uniqueidentifier")
    private UUID staffId;

    @ManyToOne
    @JoinColumn(name = "id_department", insertable = false, updatable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "id_facility", insertable = false, updatable = false)
    private Facility facility;

    @ManyToOne
    @JoinColumn(name = "id_staff", insertable = false, updatable = false)
    private Staff staff;
}