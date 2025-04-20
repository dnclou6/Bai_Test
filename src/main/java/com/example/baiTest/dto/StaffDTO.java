package com.example.baiTest.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class StaffDTO {
    private UUID id;

    @NotNull(message = "Status cannot be null")
    private Byte status;

    @NotBlank(message = "FE account cannot be blank")
    @Email(message = "Invalid FE email format")
    @Pattern(regexp = "^[^\\s]*@fe\\.edu\\.vn$", message = "FE email must end with @fe.edu.vn and contain no spaces")
    @Size(max = 100, message = "FE account must be less than 100 characters")
    private String accountFe;

    @NotBlank(message = "FPT account cannot be blank")
    @Email(message = "Invalid FPT email format")
    @Pattern(regexp = "^[^\\s]*@fpt\\.edu\\.vn$", message = "FPT email must end with @fpt.edu.vn and contain no spaces")
    @Size(max = 100, message = "FPT account must be less than 100 characters")
    private String accountFpt;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @NotBlank(message = "Staff code cannot be blank")
    @Size(max = 15, message = "Staff code must be less than 15 characters")
    private String staffCode;
}
