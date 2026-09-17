# web-graphql-ajax

Bài tập môn **Lập trình Web (WEBPR330479)** — ĐH Sư phạm Kỹ thuật TP.HCM.

Project B: xây dựng **GraphQL API** cho `Category` và `Product`, giao diện **Thymeleaf + jQuery AJAX** gọi thẳng vào endpoint `/graphql`.

## Công nghệ sử dụng

| Thành phần | Phiên bản |
|---|---|
| Java | 17 |
| Spring Boot | 3.1.5 |
| Spring Boot Starter GraphQL | theo Boot 3.1.5 |
| Spring Data JPA, Thymeleaf, Lombok | theo Boot 3.1.5 |
| SQL Server (mssql-jdbc) | theo Boot 3.1.5 |
| commons-io | 2.11.0 |
| jQuery | 3.7.1 (CDN) |
| Bootstrap | 5.3.2 (CDN) |

## Cấu hình database

Sửa `src/main/resources/application.properties` cho đúng máy của bạn, rồi chạy
`src/main/resources/sql/init.sql` trong SSMS (hoặc để `ddl-auto=update` tự tạo bảng).

> Có thể dùng chung database `WebPRDB` với Project A. Nếu muốn chạy **đồng thời** hai project thì đổi `server.port` của một trong hai (ví dụ project này thành 8083) vì cả hai đều mặc định 8082.

## Cách chạy

```bash
mvn clean install
mvn spring-boot:run
```

| Địa chỉ | Mô tả |
|---|---|
| http://localhost:8082/ | Trang chủ: sản phẩm sắp xếp giá tăng dần + lọc theo danh mục |
| http://localhost:8082/category-manage | CRUD danh mục |
| http://localhost:8082/product-manage | CRUD sản phẩm |
| http://localhost:8082/graphiql | GraphiQL để test query/mutation |
| http://localhost:8082/graphql | Endpoint GraphQL (POST, JSON) |

## Danh sách Query / Mutation

### Query

| Tên | Tham số | Trả về |
|---|---|---|
| `allProductsSortedByPrice` | — | `[Product]` |
| `productsByCategory` | `categoryId: ID!` | `[Product]` |
| `searchProducts` | `keyword, page, size` | `ProductPage` |
| `allCategories` | — | `[Category]` |
| `searchCategories` | `keyword, page, size` | `CategoryPage` |
| `productById` | `id: ID!` | `Product` |
| `categoryById` | `id: ID!` | `Category` |

### Mutation

| Tên | Tham số |
|---|---|
| `createProduct` | `input: ProductInput!` |
| `updateProduct` | `id: ID!, input: ProductInput!` |
| `deleteProduct` | `id: ID!` |
| `createCategory` | `input: CategoryInput!` |
| `updateCategory` | `id: ID!, input: CategoryInput!` |
| `deleteCategory` | `id: ID!` |

### Upload ảnh

GraphQL không hỗ trợ upload file nhị phân, nên ảnh đi qua REST rồi mới truyền tên file vào mutation:

| Method | URL | Tham số | Trả về |
|---|---|---|---|
| POST | `/api/upload` | `file` (multipart) | `Response.body` = tên file |
| GET | `/admin/products/images/{filename}` | — | ảnh |
| GET | `/admin/categories/images/{filename}` | — | ảnh |

## Ảnh chụp màn hình cần chèn khi nộp

- `docs/01-home-sorted.png` — trang chủ, sản phẩm sắp xếp giá tăng dần
- `docs/02-home-by-category.png` — lọc sản phẩm theo danh mục
- `docs/03-category-crud.png` — CRUD danh mục
- `docs/04-product-crud.png` — CRUD sản phẩm có tìm kiếm + phân trang
- `docs/05-graphiql-query.png` — chạy query trên GraphiQL
- `docs/06-graphiql-mutation.png` — chạy mutation trên GraphiQL

## Ghi chú kỹ thuật

- Field `Product.category` và `Category.products` được xử lý bằng `@SchemaMapping` — chỉ truy vấn khi client thực sự hỏi tới, tránh `LazyInitializationException` và vòng lặp vô hạn.
- `createDate` khai báo kiểu `String` trong schema; `@SchemaMapping` format sang ISO-8601. Muốn dùng scalar `DateTime` thật thì thêm `graphql-java-extended-scalars` (xem chú thích trong `schema.graphqls`).
- Mọi lỗi nghiệp vụ của GraphQL đều trả HTTP 200 kèm mảng `errors`, nên hàm `gql()` trong `graphql.js` luôn kiểm tra `data.errors` trước.
