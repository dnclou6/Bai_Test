package com.example.baiTest.service;


import com.example.baiTest.entity.*;
import com.example.baiTest.repository.*;
import com.example.baiTest.dto.ImportDTO;
import com.example.baiTest.dto.StaffDTO;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Validator;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final StaffRepository staffRepository;
    private final StaffMajorFacilityRepository staffMajorFacilityRepository;
    private final MajorFacilityRepository majorFacilityRepository;
    private final DepartmentFacilityRepository departmentFacilityRepository;
    private final ImportHistoryRepository importHistoryRepository;
    private final FacilityRepository facilityRepository;
    private final Validator validator;

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }


    public List<ImportHistory> getImportHistory() {
        return importHistoryRepository.findAll();
    }

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public List<StaffMajorFacility> getStaffMajorFacilities(UUID staffId) {
        return staffMajorFacilityRepository.findByStaffId(staffId);
    }

    public Staff getStaffById(UUID id) {
        return staffRepository.findById(id).orElse(null);
    }

    public List<Staff> getAllStaff(String sortBy, String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        return staffRepository.findAll(sort);
    }

    @Transactional
    public Staff createStaff(StaffDTO staffDTO) {
        // Validate DTO
        var violations = validator.validate(staffDTO);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + violations);
        }

        if (staffRepository.existsByAccountFe(staffDTO.getAccountFe())) {
            throw new IllegalArgumentException("Tài khoản FE đã tồn tại");
        }
        if (staffRepository.existsByAccountFpt(staffDTO.getAccountFpt())) {
            throw new IllegalArgumentException("Tài khoản FPT đã tồn tại");
        }
        if (staffRepository.existsByStaffCode(staffDTO.getStaffCode())) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại");
        }


        if (!staffDTO.getAccountFe().contains(staffDTO.getStaffCode()) ||
                !staffDTO.getAccountFpt().contains(staffDTO.getStaffCode())) {
            throw new IllegalArgumentException("Email phải chứa mã nhân viên");
        }

        // Map DTO to Entity
        Staff staff = new Staff();
        staff.setId(UUID.randomUUID());
        staff.setStatus(staffDTO.getStatus());
        staff.setCreatedDate(System.currentTimeMillis());
        staff.setLastModifiedDate(System.currentTimeMillis());
        staff.setAccountFe(staffDTO.getAccountFe());
        staff.setAccountFpt(staffDTO.getAccountFpt());
        staff.setName(staffDTO.getName());
        staff.setStaffCode(staffDTO.getStaffCode());

        return staffRepository.save(staff);
    }

    @Transactional
    public Staff updateStaff(UUID staffId, StaffDTO staffDTO) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

        var violations = validator.validate(staffDTO);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + violations);
        }

        if (!staff.getAccountFe().equals(staffDTO.getAccountFe()) &&
                staffRepository.existsByAccountFe(staffDTO.getAccountFe())) {
            throw new IllegalArgumentException("Tài khoản FE đã tồn tại");
        }
        if (!staff.getAccountFpt().equals(staffDTO.getAccountFpt()) &&
                staffRepository.existsByAccountFpt(staffDTO.getAccountFpt())) {
            throw new IllegalArgumentException("Tài khoản FPT đã tồn tại");
        }
        if (!staff.getStaffCode().equals(staffDTO.getStaffCode()) &&
                staffRepository.existsByStaffCode(staffDTO.getStaffCode())) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại");
        }

        if (!staffDTO.getAccountFe().contains(staffDTO.getStaffCode()) ||
                !staffDTO.getAccountFpt().contains(staffDTO.getStaffCode())) {
            throw new IllegalArgumentException("Email phải chứa mã nhân viên");
        }

        staff.setStatus(staffDTO.getStatus());
        staff.setLastModifiedDate(System.currentTimeMillis());
        staff.setAccountFe(staffDTO.getAccountFe());
        staff.setAccountFpt(staffDTO.getAccountFpt());
        staff.setName(staffDTO.getName());
        staff.setStaffCode(staffDTO.getStaffCode());

        return staffRepository.save(staff);
    }

    @Transactional
    public Staff toggleStaffStatus(UUID staffId) {
        Staff staff = staffRepository.findById(staffId).orElseThrow(() -> new IllegalArgumentException("Staff not found"));
        staff.setStatus(staff.getStatus() == 1 ? (byte) 0 : (byte) 1);
        staff.setLastModifiedDate(System.currentTimeMillis());
        return staffRepository.save(staff);
    }

    @Transactional
    public void assignMajorFacility(UUID staffId, UUID majorFacilityId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));
        MajorFacility majorFacility = majorFacilityRepository.findById(majorFacilityId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyên ngành"));

        // Check if staff already has a major in this facility
        UUID facilityId = majorFacility.getDepartmentFacility().getFacilityId();
        if (staffMajorFacilityRepository.findByStaffIdAndFacilityId(staffId, facilityId).isPresent()) {
            throw new IllegalArgumentException("Nhân viên đã được phân công vào một chuyên ngành trong cơ sở này");
        }

        StaffMajorFacility smf = new StaffMajorFacility();
        smf.setId(UUID.randomUUID());
        smf.setStaffId(staffId);
        smf.setMajorFacilityId(majorFacilityId);
        smf.setStatus((byte) 1);
        smf.setCreatedDate(System.currentTimeMillis());
        smf.setLastModifiedDate(System.currentTimeMillis());

        staffMajorFacilityRepository.save(smf);
    }

    @Transactional
    public void removeMajorFacility(UUID staffMajorFacilityId) {
        StaffMajorFacility smf = staffMajorFacilityRepository.findById(staffMajorFacilityId).orElseThrow(() -> new IllegalArgumentException("Major facility not found"));
        staffMajorFacilityRepository.delete(smf);
    }

    public List<DepartmentFacility> getDepartmentFacilitiesByFacility(UUID facilityId) {
        return departmentFacilityRepository.findByFacilityId(facilityId);
    }

    public List<MajorFacility> getMajorsByFacility(UUID facilityId) {
        return majorFacilityRepository.findByFacilityId(facilityId);
    }

    public List<MajorFacility> getMajorsByDepartment(UUID departmentId) {
        return majorFacilityRepository.findByDepartmentFacilityId(departmentId);
    }

    @Transactional
    public List<ImportDTO> importStaff(MultipartFile file) {
        List<ImportDTO> results = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua header
                ImportDTO result = new ImportDTO();
                result.setRowNumber(row.getRowNum() + 1);
                try {
                    // Đọc dữ liệu từ file Excel
                    StaffDTO staffDTO = new StaffDTO();
                    staffDTO.setAccountFe(getCellValueAsString(row.getCell(0)));
                    staffDTO.setAccountFpt(getCellValueAsString(row.getCell(1)));
                    staffDTO.setName(getCellValueAsString(row.getCell(2)));
                    staffDTO.setStaffCode(getCellValueAsString(row.getCell(3)));
                    staffDTO.setFacility(getCellValueAsString(row.getCell(4)));
                    staffDTO.setDepartment(getCellValueAsString(row.getCell(5)));
                    staffDTO.setMajor(getCellValueAsString(row.getCell(6)));
                    staffDTO.setStatus((byte) 1);

                    // Tạo nhân viên
                    Staff staff = createStaff(staffDTO);

                    // Xử lý Facility, Department, và Major nếu có
                    if (staffDTO.getFacility() != null && !staffDTO.getFacility().isEmpty() &&
                            staffDTO.getDepartment() != null && !staffDTO.getDepartment().isEmpty() &&
                            staffDTO.getMajor() != null && !staffDTO.getMajor().isEmpty()) {

                        // Tìm Facility
                        Facility facility = facilityRepository.findByName(staffDTO.getFacility())
                                .orElseThrow(() -> new IllegalArgumentException("Cơ sở không tồn tại: " + staffDTO.getFacility()));

                        // Tìm DepartmentFacility
                        DepartmentFacility departmentFacility = departmentFacilityRepository
                                .findByFacilityIdAndDepartmentName(facility.getId(), staffDTO.getDepartment())
                                .orElseThrow(() -> new IllegalArgumentException("Bộ môn không tồn tại: " + staffDTO.getDepartment() + " tại cơ sở " + staffDTO.getFacility()));

                        // Tìm MajorFacility
                        MajorFacility majorFacility = majorFacilityRepository
                                .findByDepartmentFacilityIdAndMajorName(departmentFacility.getId(), staffDTO.getMajor())
                                .orElseThrow(() -> new IllegalArgumentException("Chuyên ngành không tồn tại: " + staffDTO.getMajor() + " trong bộ môn " + staffDTO.getDepartment()));

                        // Gán chuyên ngành cho nhân viên
                        assignMajorFacility(staff.getId(), majorFacility.getId());
                    }

                    result.setSuccess(true);
                    result.setMessage("Import thành công: " + staffDTO.getStaffCode());
                } catch (Exception e) {
                    result.setSuccess(false);
                    result.setMessage("Lỗi tại dòng " + result.getRowNumber() + ": " + e.getMessage());
                }
                results.add(result);
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể xử lý file Excel: " + e.getMessage(), e);
        }

        // Lưu lịch sử import
        ImportHistory history = new ImportHistory();
        history.setId(UUID.randomUUID());
        history.setImportDate(System.currentTimeMillis());
        history.setFileName(file.getOriginalFilename());
        history.setStatus(results.stream().allMatch(ImportDTO::isSuccess) ? "SUCCESS" : "FAIL");
        history.setDetails(results.toString());
        importHistoryRepository.save(history);

        return results;
    }

    // Hàm hỗ trợ để lấy giá trị ô dưới dạng String
    private String getCellValueAsString(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    public byte[] downloadTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Staff Template");

            // Tạo header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Account FE");
            header.createCell(1).setCellValue("Account FPT");
            header.createCell(2).setCellValue("Name");
            header.createCell(3).setCellValue("Staff Code");
            header.createCell(4).setCellValue("Facility Name");
            header.createCell(5).setCellValue("Department Name");
            header.createCell(6).setCellValue("Major Name");

            // Tạo dữ liệu mẫu
            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("STAFF001@fe.edu.vn");
            row1.createCell(1).setCellValue("STAFF001@fpt.edu.vn");
            row1.createCell(2).setCellValue("Nguyen Van A");
            row1.createCell(3).setCellValue("STAFF001");
            row1.createCell(4).setCellValue("HN");
            row1.createCell(5).setCellValue("Department 1");
            row1.createCell(6).setCellValue("Major One");

            // Tùy chỉnh kích thước cột (tùy chọn)
            for (int i = 0; i <= 6; i++) {
                sheet.autoSizeColumn(i);
            }

            // Xuất workbook thành mảng byte
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public List<Staff> getFilteredStaff(String search, String status, String sortBy, String sortDir) {
        // Xây dựng Specification để lọc
        Specification<Staff> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo từ khóa tìm kiếm
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("staffCode")), searchPattern),
                        cb.like(cb.lower(root.get("name")), searchPattern),
                        cb.like(cb.lower(root.get("accountFe")), searchPattern),
                        cb.like(cb.lower(root.get("accountFpt")), searchPattern)
                ));
            }

            // Lọc theo trạng thái
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), Integer.parseInt(status)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Sắp xếp kết quả
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);

        // Truy vấn dữ liệu với Specification và Sort
        return staffRepository.findAll(spec, sort);
    }
}
