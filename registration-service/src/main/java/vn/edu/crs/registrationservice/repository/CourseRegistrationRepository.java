package vn.edu.crs.registrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.crs.registrationservice.entity.CourseRegistration;
import java.util.List;

public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {
    // Tìm các môn học mà một sinh viên đã đăng ký
    List<CourseRegistration> findByStudentId(String studentId);

    // Kiểm tra xem sinh viên đã đăng ký môn này chưa (tránh đăng ký trùng)
    boolean existsByStudentIdAndCourseId(String studentId, Long courseId);
}