package vn.edu.crs.registrationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import vn.edu.crs.registrationservice.dto.RegistrationRequestDTO;
import vn.edu.crs.registrationservice.entity.CourseRegistration;
import vn.edu.crs.registrationservice.repository.CourseRegistrationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseRegistrationService {

    private final CourseRegistrationRepository repository;
    private final RestTemplate restTemplate;

    private final String COURSE_SERVICE_URL = "http://localhost:8082/courses/";

    public CourseRegistration registerCourse(RegistrationRequestDTO request) {

        // 1. KIỂM TRA MÔN HỌC
        try {
            restTemplate.getForObject(COURSE_SERVICE_URL + request.getCourseId(), Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Mon hoc khong ton tai");
        } catch (Exception e) {
            throw new RuntimeException("Khong the ket noi toi course-service");
        }

        // 2. KIỂM TRA TRÙNG LẶP
        if (repository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())) {
            throw new RuntimeException("Sinh vien da dang ky mon hoc nay roi");
        }

        // 3. GỌI LIÊN THÔNG TRỪ SỐ CHỖ
        try {
            restTemplate.put(COURSE_SERVICE_URL + request.getCourseId() + "/decrease-seats", null);
        } catch (HttpClientErrorException.Conflict e) {
            // Bắt lỗi 409 từ course-service gửi về (Hết chỗ)
            throw new RuntimeException("Mon hoc da het cho");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật số chỗ bên course-service: " + e.getMessage());
        }

        // 4. LƯU DỮ LIỆU
        CourseRegistration registration = CourseRegistration.builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .trangThai("DA_DANG_KY")
                .build();

        return repository.save(registration);
    }

    // Lấy danh sách môn học một sinh viên đã đăng ký
    public List<CourseRegistration> getRegistrationsByStudentId(String studentId) {
        return repository.findByStudentId(studentId);
    }

    // API 3: Hủy đăng ký môn học
    public void cancelRegistration(Long id) {
        // 1. Tìm bản ghi đăng ký trong DB
        CourseRegistration registration = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đăng ký với ID: " + id));

        // 2. Đổi trạng thái thành DA_HUY
        registration.setTrangThai("DA_HUY");
        repository.save(registration);

        // 3. GỌI LIÊN THÔNG sang course-service để CỘNG LẠI 1 CHỖ
        try {
            restTemplate.put(COURSE_SERVICE_URL + registration.getCourseId() + "/increase-seats", null);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật số chỗ bên course-service");
        }
    }
}