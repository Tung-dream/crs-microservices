package vn.edu.crs.registrationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.edu.crs.registrationservice.dto.RegistrationRequestDTO;
import vn.edu.crs.registrationservice.entity.CourseRegistration;
import vn.edu.crs.registrationservice.service.CourseRegistrationService;

import java.util.List;

@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
public class CourseRegistrationController {

    private final CourseRegistrationService registrationService;

    // API 1: Đăng ký môn học mới
    @PostMapping
    public ResponseEntity<?> registerCourse(@Valid @RequestBody RegistrationRequestDTO request) {
        try {
            CourseRegistration savedRegistration = registrationService.registerCourse(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRegistration);
        } catch (RuntimeException e) {
            // Đổi từ badRequest() sang status(HttpStatus.CONFLICT) để trả về 409
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // API 2: Lấy danh sách các môn học mà 1 sinh viên đã đăng ký
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CourseRegistration>> getRegistrationsByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByStudentId(studentId));
    }

    // API 3: Hủy đăng ký môn học
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long id) {
        try {
            registrationService.cancelRegistration(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // Trả về 404 nếu không tìm thấy ID đăng ký để hủy
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}