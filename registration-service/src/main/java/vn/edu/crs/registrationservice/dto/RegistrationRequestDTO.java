package vn.edu.crs.registrationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationRequestDTO {

    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentId;

    @NotNull(message = "ID môn học không được để trống")
    private Long courseId;
}