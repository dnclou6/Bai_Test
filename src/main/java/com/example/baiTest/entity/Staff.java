package com.example.baiTest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;
@Entity
@Table(name = "staff")
@Data
public class Staff {
    @Id
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    private Byte status;
    @Column(name = "created_date")
    private Long createdDate;
    @Column(name = "last_modified_date")
    private Long lastModifiedDate;
    @Column(name = "account_fe")
    private String accountFe;
    @Column(name = "account_fpt")
    private String accountFpt;
    private String name;
    @Column(name = "staff_code")
    private String staffCode;
}
