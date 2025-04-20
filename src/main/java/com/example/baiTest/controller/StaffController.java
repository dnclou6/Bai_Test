package com.example.baiTest.controller;



import com.example.baiTest.dto.ImportDTO;
import com.example.baiTest.entity.DepartmentFacility;
import com.example.baiTest.entity.MajorFacility;
import com.example.baiTest.entity.Staff;
import com.example.baiTest.entity.StaffMajorFacility;
import com.example.baiTest.service.StaffService;
import com.example.baiTest.dto.StaffDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public String listStaff(Model model,
                            @RequestParam(defaultValue = "staffCode") String sortBy,
                            @RequestParam(defaultValue = "asc") String sortDir) {
        model.addAttribute("staffList", staffService.getAllStaff(sortBy, sortDir));
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("staffDTO", new StaffDTO()); // For create modal form
        return "staff/list";
    }

    // Thêm nhân viên - AJAX endpoint
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createStaff(@Valid @ModelAttribute("staffDTO") StaffDTO staffDTO, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put("success", false);
            Map<String, List<String>> fieldErrors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String fieldName = error.getField();
                String errorMessage = error.getDefaultMessage();
                fieldErrors.computeIfAbsent(fieldName, k -> new java.util.ArrayList<>()).add(errorMessage);
            }
            response.put("fieldErrors", fieldErrors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            staffService.createStaff(staffDTO);
            response.put("success", true);
            response.put("message", "Thêm nhân viên thành công!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Cập nhật nhân viên - AJAX endpoint
    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateStaff(@PathVariable UUID id, @Valid @ModelAttribute("staffDTO") StaffDTO staffDTO, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put("success", false);
            Map<String, List<String>> fieldErrors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String fieldName = error.getField();
                String errorMessage = error.getDefaultMessage();
                fieldErrors.computeIfAbsent(fieldName, k -> new java.util.ArrayList<>()).add(errorMessage);
            }
            response.put("fieldErrors", fieldErrors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            staffService.updateStaff(id, staffDTO);
            response.put("success", true);
            response.put("message", "Cập nhật nhân viên thành công!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Thêm nhân viên
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createStaffFallback(@Valid @ModelAttribute("staffDTO") StaffDTO staffDTO, BindingResult result,
                                      Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("staffList", staffService.getAllStaff());
            return "staff/list";
        }
        try {
            staffService.createStaff(staffDTO);
            redirectAttributes.addFlashAttribute("message", "Thêm nhân viên thành công!");
            return "redirect:/staff";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("staffList", staffService.getAllStaff());
            return "staff/list";
        }
    }

    // Cập nhật nhân viên
    @PostMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updateStaffFallback(@PathVariable UUID id, @Valid @ModelAttribute("staffDTO") StaffDTO staffDTO,
                                      BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("staffList", staffService.getAllStaff());
            return "staff/list";
        }
        try {
            staffService.updateStaff(id, staffDTO);
            redirectAttributes.addFlashAttribute("message", "Cập nhật nhân viên thành công!");
            return "redirect:/staff";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("staffList", staffService.getAllStaff());
            return "staff/list";
        }
    }

    //Đổi trạng thái
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        System.out.println("Toggle status called for ID: " + id);
        try {
            staffService.toggleStaffStatus(id);
            redirectAttributes.addFlashAttribute("message", "Đổi trạng thái thành công!");
            return "redirect:/staff";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/staff";
        }
    }

    //Form gán bộ môn
    @GetMapping("/assign-major/{id}")
    public String assignMajorForm(@PathVariable UUID id, Model model) {
        Staff staff = staffService.getStaffById(id);
        if (staff == null) {
            throw new IllegalArgumentException("Staff not found");
        }
        model.addAttribute("staff", staff);

        List<StaffMajorFacility> staffMajorFacilities = staffService.getStaffMajorFacilities(id);
        model.addAttribute("staffMajorFacilities", staffMajorFacilities);
        model.addAttribute("facilities", staffService.getAllFacilities());

        return "staff/assign-major";
    }

    //Form gán bộ môn
    @PostMapping("/assign-major")
    public String assignMajorFacility(@RequestParam UUID staffId, @RequestParam UUID majorFacilityId, RedirectAttributes redirectAttributes) {
        try {
            staffService.assignMajorFacility(staffId, majorFacilityId);
            redirectAttributes.addFlashAttribute("message", "Gán bộ môn thành công!");
            return "redirect:/staff/assign-major/" + staffId;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/staff/assign-major/" + staffId;
        }
    }

    // Xóa bộ môn
    @PostMapping("/remove-major/{staffMajorFacilityId}")
    public String removeMajorFacility(@PathVariable UUID staffMajorFacilityId, RedirectAttributes redirectAttributes) {
        try {
            staffService.removeMajorFacility(staffMajorFacilityId);
            redirectAttributes.addFlashAttribute("message", "Xóa bộ môn thành công!");
            return "redirect:/staff";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/staff";
        }
    }

    @GetMapping("/import")
    public String importStaffForm(Model model) {
        return "staff/import";
    }

    @PostMapping("/import")
    public String importStaff(@RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        try {
            List<ImportDTO> results = staffService.importStaff(file);
            model.addAttribute("results", results);
            return "staff/import";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "staff/import";
        }
    }

    @GetMapping("/history")
    public String listImportHistory(Model model) {
        model.addAttribute("historyList", staffService.getImportHistory());
        return "history";
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        byte[] template = staffService.downloadTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=staff_template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(template);
    }

    @GetMapping("/api/facilities/{facilityId}/departments")
    @ResponseBody
    public List<DepartmentFacility> getDepartmentsByFacility(@PathVariable UUID facilityId) {
        return staffService.getDepartmentFacilitiesByFacility(facilityId);
    }

    @GetMapping("/api/departments/{departmentId}/majors")
    @ResponseBody
    public List<MajorFacility> getMajorsByDepartment(@PathVariable UUID departmentId) {
        return staffService.getMajorsByDepartment(departmentId);
    }

    @PostMapping("/api/toggle-status/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleStatusAjax(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            staffService.toggleStaffStatus(id);
            response.put("success", true);
            response.put("message", "Đổi trạng thái thành công!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
