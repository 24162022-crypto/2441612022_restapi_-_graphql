# web-restapi-ajax

Bài tập môn **Lập trình Web (WEBPR330479)** — ĐH Sư phạm Kỹ thuật TP.HCM.

Project A: xây dựng **REST API** quản lý `Category` và `Product`, giao diện **Thymeleaf + jQuery AJAX**, upload ảnh, tìm kiếm và phân trang.

## Công nghệ sử dụng

| Thành phần | Phiên bản |
|---|---|
| Java | 17 |
| Spring Boot | 3.1.5 |
| Spring Web, Spring Data JPA | theo Boot 3.1.5 |
| Thymeleaf | theo Boot 3.1.5 |
| Lombok | theo Boot 3.1.5 |
| SQL Server (mssql-jdbc) | theo Boot 3.1.5 |
| springdoc-openapi (Swagger 3) | 2.0.2 |
| commons-io | 2.11.0 |
| jQuery | 3.7.1 (CDN) |
| Bootstrap | 5.3.2 (CDN) |

## Cấu hình database

Mở `src/main/resources/application.properties` và sửa cho đúng máy của bạn:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=WebPRDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=<mật khẩu của bạn>
```

Chạy `src/main/resources/sql/init.sql` trong SSMS để tạo database và dữ liệu mẫu.
Nếu để `spring.jpa.hibernate.ddl-auto=update` thì Hibernate cũng tự tạo bảng khi khởi động.

## Cách chạy

```bash
mvn clean install
mvn spring-boot:run
```

Hoặc chạy trực tiếp `vn.iotstar.Application` trong IDE.

| Địa chỉ | Mô tả |
|---|---|
| http://localhost:8082/category | Trang quản lý danh mục |
| http://localhost:8082/product | Trang quản lý sản phẩm |
| http://localhost:8082/swagger-ui/index.html | Swagger UI |

Ảnh upload được lưu ở thư mục `uploads/` (tự tạo khi khởi động).

## Danh sách endpoint

### Category

| Method | URL | Tham số |
|---|---|---|
| GET | `/api/category` | — |
| POST | `/api/category/getCategory` | `id` |
| POST | `/api/category/addCategory` | `categoryName`, `icon` (file) |
| PUT / POST | `/api/category/updateCategory` | `categoryId`, `categoryName`, `icon` (file) |
| DELETE | `/api/category/deleteCategory` | `categoryId` |

### Product

| Method | URL | Tham số |
|---|---|---|
| GET | `/api/product` | — |
| POST | `/api/product/getProduct` | `id` |
| POST | `/api/product/addProduct` | `productName`, `imageFile`, `unitPrice`, `discount`, `description`, `categoryId`, `quantity`, `status` |
| PUT / POST | `/api/product/updateProduct` | như trên, thêm `productId` |
| DELETE | `/api/product/deleteProduct` | `productId` |
| GET | `/api/product/search` | `keyword`, `page`, `size` |

### Ảnh

| Method | URL |
|---|---|
| GET | `/admin/products/images/{filename}` |
| GET | `/admin/categories/images/{filename}` |

Mọi API đều trả về định dạng chung:

```json
{ "status": true, "message": "Thành công", "body": { } }
```

## Ảnh chụp màn hình cần chèn khi nộp

Tạo thư mục `docs/` rồi chèn các ảnh sau vào README:

- `docs/01-category-list.png` — danh sách danh mục
- `docs/02-category-add.png` — modal thêm danh mục
- `docs/03-product-list.png` — danh sách sản phẩm có phân trang
- `docs/04-product-edit.png` — modal sửa sản phẩm đã đổ sẵn dữ liệu
- `docs/05-product-search.png` — kết quả tìm kiếm
- `docs/06-swagger.png` — màn hình Swagger UI
- `docs/07-postman.png` — test API bằng Postman

Cú pháp chèn: `![Danh sách danh mục](docs/01-category-list.png)`

## Ghi chú kỹ thuật

- Cập nhật (`update...`) nhận **cả PUT và POST**. Giao diện gửi `POST` kèm `_method=PUT` vì Tomcat không tự parse `multipart/form-data` cho request `PUT`.
- `addProduct` đã sửa lỗi của code mẫu: nhận trực tiếp các `@RequestParam` thay vì copy từ một `ProductModel` rỗng.
- `Product` trả về client dưới dạng `ProductModel` để có `categoryName` mà vẫn tránh `StackOverflowError` / `LazyInitializationException`.
