-- =========================================================
-- File: src/main/resources/sql/init.sql
-- Tao database va du lieu mau - WEBPR330479
-- Luu y: bat buoc dung N'...' truoc chuoi tieng Viet
-- =========================================================

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'WebPRDB')
BEGIN
    CREATE DATABASE WebPRDB;
END
GO

USE WebPRDB;
GO

IF OBJECT_ID('dbo.Products', 'U') IS NOT NULL DROP TABLE dbo.Products;
IF OBJECT_ID('dbo.Categories', 'U') IS NOT NULL DROP TABLE dbo.Categories;
GO

CREATE TABLE Categories (
    categoryId BIGINT IDENTITY(1,1) PRIMARY KEY,
    categoryName NVARCHAR(255) NOT NULL,
    icon VARCHAR(255) NULL
);
GO

CREATE TABLE Products (
    productId BIGINT IDENTITY(1,1) PRIMARY KEY,
    productName NVARCHAR(500) NOT NULL,
    quantity INT NOT NULL,
    unitPrice FLOAT NOT NULL,
    images VARCHAR(200) NULL,
    description NVARCHAR(500) NOT NULL,
    discount FLOAT NOT NULL DEFAULT 0,
    createDate DATETIME NULL,
    status SMALLINT NOT NULL DEFAULT 1,
    categoryId BIGINT NULL,
    CONSTRAINT FK_Products_Categories FOREIGN KEY (categoryId) REFERENCES Categories(categoryId)
);
GO

INSERT INTO Categories (categoryName, icon) VALUES
(N'Điện thoại', NULL),
(N'Laptop', NULL),
(N'Đồng hồ thông minh', NULL);
GO

INSERT INTO Products (productName, quantity, unitPrice, images, description, discount, createDate, status, categoryId) VALUES
(N'iPhone 15 Pro Max', 20, 29990000, NULL, N'Điện thoại cao cấp của Apple, chip A17 Pro', 5, GETDATE(), 1, 1),
(N'Samsung Galaxy S24 Ultra', 15, 27990000, NULL, N'Flagship Android màn hình Dynamic AMOLED', 10, GETDATE(), 1, 1),
(N'Xiaomi 14', 30, 15990000, NULL, N'Điện thoại tầm trung cấu hình mạnh', 0, GETDATE(), 1, 1),
(N'MacBook Air M3', 10, 32990000, NULL, N'Laptop mỏng nhẹ, chip Apple M3', 3, GETDATE(), 1, 2),
(N'Dell XPS 13', 8, 28990000, NULL, N'Laptop cao cấp thiết kế viền mỏng', 0, GETDATE(), 1, 2),
(N'Asus ROG Strix', 5, 45990000, NULL, N'Laptop gaming hiệu năng cao', 8, GETDATE(), 1, 2),
(N'Apple Watch Series 9', 25, 10990000, NULL, N'Đồng hồ thông minh tích hợp sức khỏe', 0, GETDATE(), 1, 3),
(N'Samsung Galaxy Watch 6', 18, 7990000, NULL, N'Đồng hồ thông minh chống nước', 5, GETDATE(), 1, 3);
GO
