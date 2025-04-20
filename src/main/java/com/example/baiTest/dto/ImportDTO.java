package com.example.baiTest.dto;

import lombok.Data;

@Data
public class ImportDTO {
    private int rowNumber;
    private boolean success;
    private String message;
}
