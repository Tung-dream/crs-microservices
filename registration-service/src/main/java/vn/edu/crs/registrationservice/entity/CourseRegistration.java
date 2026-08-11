package vn.edu.crs.registrationservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_registrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private LocalDateTime registrationTime;

    @Column(nullable = false)
    private String trangThai = "DA_DANG_KY";

    @PrePersist
    protected void onCreate() {
        this.registrationTime = LocalDateTime.now();
    }
}