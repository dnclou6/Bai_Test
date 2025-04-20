package com.example.baiTest.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class StaffDTO {
    @NotNull(message = "Trạng thái không được để trống")
    private Byte status;

    @NotBlank(message = "Tài khoản FE không được để trống")
    @Email(message = "Định dạng email FE không hợp lệ")
    @Pattern(regexp = "^[^\\s]*@fe\\.edu\\.vn$", message = "Email FE phải kết thúc bằng @fe.edu.vn và không chứa khoảng trắng")
    @Size(max = 100, message = "Tài khoản FE phải dưới 100 ký tự")
    private String accountFe;

    @NotBlank(message = "Tài khoản FPT không được để trống")
    @Email(message = "Định dạng email FPT không hợp lệ")
    @Pattern(regexp = "^[^\\s]*@fpt\\.edu\\.vn$", message = "Email FPT phải kết thúc bằng @fpt.edu.vn và không chứa khoảng trắng")
    @Size(max = 100, message = "Tài khoản FPT phải dưới 100 ký tự")
    private String accountFpt;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên phải dưới 100 ký tự")
    private String name;

    @NotBlank(message = "Mã nhân viên không được để trống")
    @Size(max = 15, message = "Mã nhân viên phải dưới 15 ký tự")
    private String staffCode;

    private String department;

    private String major;

    private String facility;
}
