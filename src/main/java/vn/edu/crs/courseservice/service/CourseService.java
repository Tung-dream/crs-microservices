package vn.edu.crs.courseservice.service;

import vn.edu.crs.courseservice.dto.CourseDTO;
import vn.edu.crs.courseservice.entity.Course;
import vn.edu.crs.courseservice.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    public List<CourseDTO> getAll() {
        return courseRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    public CourseDTO getById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay mon hoc id = " + id));
        return toDTO(course);
    }
    public CourseDTO create(CourseDTO dto) {
        if
        (courseRepository.existsByTenMonHocIgnoreCase(dto.getTenMonHoc())) {
            throw new IllegalArgumentException("Ten mon hoc da ton tai");
        }
        Course course = new Course();
        course.setTenMonHoc(dto.getTenMonHoc());
        course.setSoTinChi(dto.getSoTinChi());
        course.setSoChoToiDa(dto.getSoChoToiDa());

// Quy tac nghiep vu: khi tao moi, so cho con lai luon bang so cho toi da
        course.setSoChoConLai(dto.getSoChoToiDa());
        return toDTO(courseRepository.save(course));
    }
    public CourseDTO update(Long id, CourseDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay mon hoc id = " + id));
                        course.setTenMonHoc(dto.getTenMonHoc());
        course.setSoTinChi(dto.getSoTinChi());
        course.setSoChoToiDa(dto.getSoChoToiDa());
        return toDTO(courseRepository.save(course));
    }

    public Page<CourseDTO> search(String keyword, Pageable pageable) {
        Page<Course> page = (keyword == null || keyword.isBlank())
                ? courseRepository.findAll(pageable)
                : courseRepository.findByTenMonHocContainingIgnoreCase(keyword, pageable);
        return page.map(this::toDTO);
    }

    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new NoSuchElementException("Khong tim thay mon hoc id = " + id);
        }
        courseRepository.deleteById(id);
    }

    // API hỗ trợ giảm số chỗ còn lại khi có sinh viên đăng ký
    public void decreaseSeats(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay mon hoc id = " + id));

        // Kiểm tra xem còn chỗ không
        if (course.getSoChoConLai() <= 0) {
            throw new IllegalStateException("Mon hoc da het cho");
        }

        course.setSoChoConLai(course.getSoChoConLai() - 1);
        courseRepository.save(course);
    }

    // API hỗ trợ Tăng lại số chỗ khi sinh viên hủy đăng ký
    public void increaseSeats(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay mon hoc id = " + id));

        // (Tùy chọn) Đảm bảo số chỗ còn lại không vượt quá số chỗ tối đa
        if (course.getSoChoConLai() < course.getSoChoToiDa()) {
            course.setSoChoConLai(course.getSoChoConLai() + 1);
            courseRepository.save(course);
        }
    }

    private CourseDTO toDTO(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getTenMonHoc(),
                course.getSoTinChi(),
                course.getSoChoToiDa(),
                course.getSoChoConLai()
        );
    }
}
