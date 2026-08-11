package vn.edu.crs.courseservice.controller;

import vn.edu.crs.courseservice.dto.CourseDTO;
import vn.edu.crs.courseservice.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public Page<CourseDTO> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "tenMonHoc,asc") String[] sort) {

        // Lấy thông tin cột cần sắp xếp và chiều sắp xếp
        String sortField = sort[0];
        Sort.Direction sortDirection = (sort.length > 1 && sort[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        // Chủ động khởi tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));

        return courseService.search(keyword, pageable);
    }

    @GetMapping("/{id}")
    public CourseDTO getById(@PathVariable Long id) {
        return courseService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDTO create(@Valid @RequestBody CourseDTO dto) {
        return courseService.create(dto);
    }

    @PutMapping("/{id}")
    public CourseDTO update(@PathVariable Long id, @Valid @RequestBody CourseDTO dto) {
        return courseService.update(id, dto);
    }

    // API hỗ trợ Test 1 & 4: Giảm số chỗ (khi có người đăng ký)
    @PutMapping("/{id}/decrease-seats")
    public ResponseEntity<?> decreaseSeats(@PathVariable Long id) {
        try {
            courseService.decreaseSeats(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            // Nếu hết chỗ, ném ra lỗi 409 Conflict (đúng yêu cầu Test 4)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // API hỗ trợ Test 3: Tăng số chỗ (khi có người hủy đăng ký)
    @PutMapping("/{id}/increase-seats")
    public ResponseEntity<?> increaseSeats(@PathVariable Long id) {
        try {
            courseService.increaseSeats(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        courseService.delete(id);
    }
}