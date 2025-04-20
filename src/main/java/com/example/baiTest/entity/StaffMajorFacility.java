package com.example.baiTest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "staff_major_facility")
@Data
public class StaffMajorFacility {
    @Id
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    private Byte status;
    @Column(name = "created_date")
    private Long createdDate;
    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    @Column(name = "id_major_facility", columnDefinition = "uniqueidentifier")
    private UUID majorFacilityId;

    @Column(name = "id_staff", columnDefinition = "uniqueidentifier")
    private UUID staffId;

    @ManyToOne
    @JoinColumn(name = "id_major_facility", insertable = false, updatable = false)
    private MajorFacility majorFacility;

    @ManyToOne
    @JoinColumn(name = "id_staff", insertable = false, updatable = false)
    private Staff staff;
}
