# THIẾT KẾ BIÊN GIỚI SERVICE

## 1. Danh sách Service
- api-gateway (Port 8080): Điểm vào duy nhất, định tuyến, xác thực sơ bộ.
- auth-service (Port 8081, DB: auth_db): Quản lý User, Student, đăng nhập, sinh/xác thực JWT.
- course-service (Port 8082, DB: course_db): Quản lý Course, tìm kiếm, phân trang, quản lý số chỗ.
- registration-service (Port 8083, DB: registration_db): Quản lý Registration, gọi sang course-service để đăng ký.

## 2. Nguyên tắc sở hữu dữ liệu (Data Ownership)
- Mỗi service có DATABASE RIÊNG, KHÔNG service nào được truy cập trực tiếp DB của service khác.
- Muốn lấy/thay đổi dữ liệu của service khác -> PHẢI gọi REST API sang service đó.

## 3. Bảng định tuyến Gateway (dự kiến)
- /api/auth/** -> http://localhost:8081
- /api/courses/** -> http://localhost:8082
- /api/registrations/** -> http://localhost:8083