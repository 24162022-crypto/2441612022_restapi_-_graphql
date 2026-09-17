/*
 * File: src/main/resources/static/js/graphql.js
 * Goi GraphQL API bang jQuery AJAX cho toan bo giao dien.
 */

// ============ HAM DUNG CHUNG GOI GRAPHQL ============

/**
 * Ham goi GraphQL dung chung cho moi nghiep vu.
 * @param query     chuoi query/mutation
 * @param variables object bien (co the null)
 * @param onSuccess callback nhan data khi thanh cong
 * @param onError   callback nhan chuoi loi (neu bo trong se alert)
 *
 * LUU Y QUAN TRONG: GraphQL luon tra ve HTTP 200 ke ca khi co loi nghiep vu.
 * Loi nam trong truong data.errors -> phai kiem tra thu cong, khong duoc chi
 * dua vao ham error() cua jQuery.
 */
function gql(query, variables, onSuccess, onError) {
    function handleError(msg) {
        if (typeof onError === 'function') {
            onError(msg);
        } else {
            alert(msg);
        }
    }

    $.ajax({
        url: contextPath + '/graphql',
        type: 'POST',
        contentType: 'application/json',  // BAT BUOC, thieu se bi loi 415
        data: JSON.stringify({ query: query, variables: variables || {} }),
        dataType: 'json',
        success: function (data) {
            if (data.errors && data.errors.length > 0) {
                var msg = data.errors.map(function (e) { return e.message; }).join('; ');
                handleError('Loi GraphQL: ' + msg);
                return;
            }
            if (typeof onSuccess === 'function') {
                onSuccess(data.data);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            var msg;
            if (jqXHR.status === 0) {
                msg = 'Khong ket noi duoc toi server.';
            } else if (jqXHR.status === 400) {
                msg = 'Loi 400: cau truy van GraphQL sai cu phap.';
            } else if (jqXHR.status === 415) {
                msg = 'Loi 415: thieu contentType application/json.';
            } else {
                msg = 'Loi ' + jqXHR.status + ': ' + (errorThrown || textStatus);
            }
            handleError(msg);
        }
    });
}

/** Upload anh qua REST, tra ve ten file qua callback */
function uploadImage(fileInput, onDone, onError) {
    if (!fileInput || fileInput.files.length === 0) {
        onDone(null); // khong chon anh -> tra ve null
        return;
    }
    var formData = new FormData();
    formData.append('file', fileInput.files[0]);

    $.ajax({
        url: contextPath + '/api/upload',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        cache: false,
        success: function (res) {
            if (res.status) {
                onDone(res.body); // res.body la ten file
            } else {
                onError(res.message);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            onError('Upload that bai: ' + (errorThrown || textStatus));
        }
    });
}

// ============ TIEN ICH ============

function showAlert(boxId, message, isSuccess) {
    var $box = $('#' + boxId);
    $box.removeClass('d-none alert-success alert-danger');
    $box.addClass(isSuccess ? 'alert-success' : 'alert-danger');
    $box.text(message);
    setTimeout(function () { $box.addClass('d-none'); }, 4000);
}

function formatMoney(value) {
    if (value === null || value === undefined) return '';
    return Number(value).toLocaleString('vi-VN') + ' d';
}

function escapeHtml(text) {
    if (text === null || text === undefined) return '';
    return $('<div>').text(text).html();
}

function imgTag(fileName, folder, size) {
    if (!fileName) return '<span class="text-muted">-</span>';
    return '<img src="' + contextPath + '/admin/' + folder + '/images/' + fileName
        + '" width="' + size + '" height="' + size + '" style="object-fit:cover">';
}

/** Ve thanh phan trang dung chung */
function renderPagination(containerId, totalPages, currentPage, cssClass) {
    if (totalPages <= 1) {
        $('#' + containerId).html('');
        return;
    }
    var html = '';
    html += '<li class="page-item' + (currentPage === 0 ? ' disabled' : '') + '">'
        + '<a class="page-link ' + cssClass + '" href="#" data-page="' + (currentPage - 1) + '">Truoc</a></li>';
    for (var i = 0; i < totalPages; i++) {
        html += '<li class="page-item' + (i === currentPage ? ' active' : '') + '">'
            + '<a class="page-link ' + cssClass + '" href="#" data-page="' + i + '">' + (i + 1) + '</a></li>';
    }
    html += '<li class="page-item' + (currentPage >= totalPages - 1 ? ' disabled' : '') + '">'
        + '<a class="page-link ' + cssClass + '" href="#" data-page="' + (currentPage + 1) + '">Sau</a></li>';
    $('#' + containerId).html(html);
}

// ============ TRANG HOME ============

/** Section 1: tat ca san pham sap xep gia tang dan */
function loadAllProductsSortedByPrice() {
    var query = `
        query {
            allProductsSortedByPrice {
                productId
                productName
                unitPrice
                images
                discount
                category { categoryName }
            }
        }`;
    gql(query, null, function (data) {
        renderProductCards('allProductsArea', data.allProductsSortedByPrice);
    }, function (msg) {
        showAlert('homeAlert', msg, false);
    });
}

/** Section 2: san pham theo danh muc */
function loadProductsByCategory(categoryId) {
    if (!categoryId) {
        $('#categoryProductsArea').html('<p class="text-muted">Hay chon mot danh muc.</p>');
        return;
    }
    var query = `
        query($categoryId: ID!) {
            productsByCategory(categoryId: $categoryId) {
                productId
                productName
                unitPrice
                images
                discount
                category { categoryName }
            }
        }`;
    gql(query, { categoryId: categoryId }, function (data) {
        renderProductCards('categoryProductsArea', data.productsByCategory);
    }, function (msg) {
        showAlert('homeAlert', msg, false);
    });
}

/** Ve danh sach san pham dang the (card) */
function renderProductCards(areaId, list) {
    var html = '';
    if (!list || list.length === 0) {
        html = '<p class="text-muted">Khong co san pham nao.</p>';
    }
    $.each(list || [], function (i, p) {
        var img = p.images
            ? '<img src="' + contextPath + '/admin/products/images/' + p.images
                + '" class="card-img-top" style="height:160px;object-fit:cover">'
            : '<div class="bg-light d-flex align-items-center justify-content-center" '
                + 'style="height:160px">Khong co anh</div>';
        var catName = (p.category && p.category.categoryName) ? p.category.categoryName : '';
        html += '<div class="col-md-3 mb-3">'
            + '<div class="card h-100">'
            + img
            + '<div class="card-body">'
            + '<h6 class="card-title">' + escapeHtml(p.productName) + '</h6>'
            + '<p class="mb-1 text-danger fw-bold">' + formatMoney(p.unitPrice) + '</p>'
            + '<p class="mb-0"><small class="text-muted">' + escapeHtml(catName)
            + ' | Giam ' + p.discount + '%</small></p>'
            + '</div></div></div>';
    });
    $('#' + areaId).html(html);
}

/** Do danh muc vao dropdown trang home */
function loadCategoriesToSelect(selectId, selectedId, callback) {
    var query = `query { allCategories { categoryId categoryName } }`;
    gql(query, null, function (data) {
        var html = '';
        if (selectId === 'homeCategorySelect') {
            html += '<option value="">-- Chon danh muc --</option>';
        }
        $.each(data.allCategories || [], function (i, c) {
            var sel = (selectedId && String(selectedId) === String(c.categoryId)) ? ' selected' : '';
            html += '<option value="' + c.categoryId + '"' + sel + '>'
                + escapeHtml(c.categoryName) + '</option>';
        });
        $('#' + selectId).html(html);
        if (typeof callback === 'function') callback();
    }, function (msg) {
        $('#' + selectId).html('<option value="">' + msg + '</option>');
    });
}

// ============ QUAN LY CATEGORY ============

var categoryCurrentPage = 0;

function loadCategoryTable(page) {
    categoryCurrentPage = page || 0;
    var keyword = $.trim($('#categoryKeyword').val());
    var query = `
        query($keyword: String, $page: Int, $size: Int) {
            searchCategories(keyword: $keyword, page: $page, size: $size) {
                content { categoryId categoryName icon }
                totalPages
                totalElements
                currentPage
            }
        }`;
    gql(query, { keyword: keyword, page: categoryCurrentPage, size: 5 }, function (data) {
        var pageData = data.searchCategories;
        var html = '';
        if (!pageData.content || pageData.content.length === 0) {
            html = '<tr><td colspan="4" class="text-center">Khong co danh muc nao</td></tr>';
        }
        $.each(pageData.content || [], function (i, c) {
            html += '<tr>'
                + '<td>' + c.categoryId + '</td>'
                + '<td>' + escapeHtml(c.categoryName) + '</td>'
                + '<td>' + imgTag(c.icon, 'categories', 50) + '</td>'
                + '<td>'
                + '<button class="btn btn-sm btn-warning btn-edit-category" data-id="' + c.categoryId + '">Sua</button> '
                + '<button class="btn btn-sm btn-danger btn-delete-category" data-id="' + c.categoryId + '">Xoa</button>'
                + '</td></tr>';
        });
        $('#categoryTableBody').html(html);
        renderPagination('categoryPagination', pageData.totalPages, pageData.currentPage, 'page-link-category');
    }, function (msg) {
        $('#categoryTableBody').html(
            '<tr><td colspan="4" class="text-danger text-center">' + escapeHtml(msg) + '</td></tr>');
        $('#categoryPagination').empty();
    });
}

function openAddCategory() {
    $('#categoryModalTitle').text('Them danh muc');
    $('#categoryId').val('');
    $('#categoryName').val('');
    $('#categoryIcon').val('');
    $('#categoryIconName').val('');
    $('#categoryModalError').text('');
    new bootstrap.Modal(document.getElementById('categoryModal')).show();
}

function openEditCategory(id) {
    var query = `query($id: ID!) { categoryById(id: $id) { categoryId categoryName icon } }`;
    gql(query, { id: id }, function (data) {
        var c = data.categoryById;
        if (!c) {
            showAlert('categoryAlert', 'Khong tim thay danh muc', false);
            return;
        }
        $('#categoryModalTitle').text('Sua danh muc');
        $('#categoryId').val(c.categoryId);
        $('#categoryName').val(c.categoryName);
        $('#categoryIconName').val(c.icon || '');
        $('#categoryIcon').val('');
        $('#categoryModalError').text('');
        new bootstrap.Modal(document.getElementById('categoryModal')).show();
    }, function (msg) {
        showAlert('categoryAlert', msg, false);
    });
}

function saveCategory() {
    var id = $('#categoryId').val();
    var name = $.trim($('#categoryName').val());
    if (name === '') {
        $('#categoryModalError').text('Ten danh muc khong duoc de trong');
        return;
    }

    // Buoc 1: upload anh (neu co) -> Buoc 2: goi mutation voi ten file
    uploadImage(document.getElementById('categoryIcon'), function (fileName) {
        var icon = fileName ? fileName : $('#categoryIconName').val();
        var input = { categoryName: name, icon: icon };

        var query, variables;
        if (id === '') {
            query = `
                mutation($input: CategoryInput!) {
                    createCategory(input: $input) { categoryId categoryName icon }
                }`;
            variables = { input: input };
        } else {
            query = `
                mutation($id: ID!, $input: CategoryInput!) {
                    updateCategory(id: $id, input: $input) { categoryId categoryName icon }
                }`;
            variables = { id: id, input: input };
        }

        gql(query, variables, function () {
            bootstrap.Modal.getInstance(document.getElementById('categoryModal')).hide();
            showAlert('categoryAlert', 'Luu danh muc thanh cong', true);
            loadCategoryTable(categoryCurrentPage);
        }, function (msg) {
            $('#categoryModalError').text(msg);
        });
    }, function (msg) {
        $('#categoryModalError').text(msg);
    });
}

function deleteCategory(id) {
    if (!confirm('Ban chac chan muon xoa danh muc nay?')) return;
    var query = `mutation($id: ID!) { deleteCategory(id: $id) { status message } }`;
    gql(query, { id: id }, function (data) {
        var r = data.deleteCategory;
        showAlert('categoryAlert', r.message, r.status);
        loadCategoryTable(categoryCurrentPage);
    }, function (msg) {
        showAlert('categoryAlert', msg, false);
    });
}

// ============ QUAN LY PRODUCT ============

var productCurrentPage = 0;

function loadProductTable(page) {
    productCurrentPage = page || 0;
    var keyword = $.trim($('#productKeyword').val());
    var size = parseInt($('#productPageSize').val(), 10);

    var query = `
        query($keyword: String, $page: Int, $size: Int) {
            searchProducts(keyword: $keyword, page: $page, size: $size) {
                content {
                    productId productName unitPrice quantity discount images status
                    category { categoryId categoryName }
                }
                totalPages
                totalElements
                currentPage
            }
        }`;
    gql(query, { keyword: keyword, page: productCurrentPage, size: size }, function (data) {
        var pageData = data.searchProducts;
        var html = '';
        if (!pageData.content || pageData.content.length === 0) {
            html = '<tr><td colspan="9" class="text-center">Khong tim thay san pham nao</td></tr>';
        }
        $.each(pageData.content || [], function (i, p) {
            var catName = (p.category && p.category.categoryName) ? p.category.categoryName : '';
            var badge = (p.status === 1)
                ? '<span class="badge bg-success">Dang ban</span>'
                : '<span class="badge bg-secondary">Ngung ban</span>';
            html += '<tr>'
                + '<td>' + p.productId + '</td>'
                + '<td>' + imgTag(p.images, 'products', 60) + '</td>'
                + '<td>' + escapeHtml(p.productName) + '</td>'
                + '<td>' + formatMoney(p.unitPrice) + '</td>'
                + '<td>' + p.quantity + '</td>'
                + '<td>' + p.discount + '%</td>'
                + '<td>' + escapeHtml(catName) + '</td>'
                + '<td>' + badge + '</td>'
                + '<td>'
                + '<button class="btn btn-sm btn-warning btn-edit-product" data-id="' + p.productId + '">Sua</button> '
                + '<button class="btn btn-sm btn-danger btn-delete-product" data-id="' + p.productId + '">Xoa</button>'
                + '</td></tr>';
        });
        $('#productTableBody').html(html);
        renderPagination('productPagination', pageData.totalPages, pageData.currentPage, 'page-link-product');
    }, function (msg) {
        $('#productTableBody').html(
            '<tr><td colspan="9" class="text-danger text-center">' + escapeHtml(msg) + '</td></tr>');
        $('#productPagination').empty();
    });
}

function openAddProduct() {
    $('#productModalTitle').text('Them san pham');
    $('#productId').val('');
    $('#productName').val('');
    $('#productUnitPrice').val(0);
    $('#productQuantity').val(0);
    $('#productDiscount').val(0);
    $('#productDescription').val('');
    $('#productImageFile').val('');
    $('#productImageName').val('');
    $('#productStatus').val('1');
    $('#productModalError').text('');
    loadCategoriesToSelect('productCategoryId', null, function () {
        new bootstrap.Modal(document.getElementById('productModal')).show();
    });
}

function openEditProduct(id) {
    var query = `
        query($id: ID!) {
            productById(id: $id) {
                productId productName unitPrice quantity discount description images status
                category { categoryId }
            }
        }`;
    gql(query, { id: id }, function (data) {
        var p = data.productById;
        if (!p) {
            showAlert('productAlert', 'Khong tim thay san pham', false);
            return;
        }
        $('#productModalTitle').text('Sua san pham');
        $('#productId').val(p.productId);
        $('#productName').val(p.productName);
        $('#productUnitPrice').val(p.unitPrice);
        $('#productQuantity').val(p.quantity);
        $('#productDiscount').val(p.discount);
        $('#productDescription').val(p.description);
        $('#productImageName').val(p.images || '');
        $('#productImageFile').val('');
        $('#productStatus').val(p.status);
        $('#productModalError').text('');
        var catId = (p.category ? p.category.categoryId : null);
        loadCategoriesToSelect('productCategoryId', catId, function () {
            new bootstrap.Modal(document.getElementById('productModal')).show();
        });
    }, function (msg) {
        showAlert('productAlert', msg, false);
    });
}

function saveProduct() {
    var id = $('#productId').val();
    var name = $.trim($('#productName').val());
    var description = $.trim($('#productDescription').val());
    var categoryId = $('#productCategoryId').val();

    if (name === '') {
        $('#productModalError').text('Ten san pham khong duoc de trong');
        return;
    }
    if (!categoryId) {
        $('#productModalError').text('Chua chon danh muc. Hay tao danh muc truoc.');
        return;
    }
    if (description === '') {
        $('#productModalError').text('Mo ta khong duoc de trong');
        return;
    }

    uploadImage(document.getElementById('productImageFile'), function (fileName) {
        var images = fileName ? fileName : $('#productImageName').val();
        var input = {
            productName: name,
            quantity: parseInt($('#productQuantity').val(), 10) || 0,
            unitPrice: parseFloat($('#productUnitPrice').val()) || 0,
            images: images,
            description: description,
            discount: parseFloat($('#productDiscount').val()) || 0,
            status: parseInt($('#productStatus').val(), 10),
            categoryId: categoryId
        };

        var query, variables;
        if (id === '') {
            query = `
                mutation($input: ProductInput!) {
                    createProduct(input: $input) { productId productName }
                }`;
            variables = { input: input };
        } else {
            query = `
                mutation($id: ID!, $input: ProductInput!) {
                    updateProduct(id: $id, input: $input) { productId productName }
                }`;
            variables = { id: id, input: input };
        }

        gql(query, variables, function () {
            bootstrap.Modal.getInstance(document.getElementById('productModal')).hide();
            showAlert('productAlert', 'Luu san pham thanh cong', true);
            loadProductTable(productCurrentPage);
        }, function (msg) {
            $('#productModalError').text(msg);
        });
    }, function (msg) {
        $('#productModalError').text(msg);
    });
}

function deleteProduct(id) {
    if (!confirm('Ban chac chan muon xoa san pham nay?')) return;
    var query = `mutation($id: ID!) { deleteProduct(id: $id) { status message } }`;
    gql(query, { id: id }, function (data) {
        var r = data.deleteProduct;
        showAlert('productAlert', r.message, r.status);
        loadProductTable(productCurrentPage);
    }, function (msg) {
        showAlert('productAlert', msg, false);
    });
}

// ============ GAN SU KIEN ============
$(document).ready(function () {

    // --- Trang home ---
    if ($('#allProductsArea').length > 0) {
        loadAllProductsSortedByPrice();
        loadCategoriesToSelect('homeCategorySelect', null, null);
        $('#homeCategorySelect').on('change', function () {
            loadProductsByCategory($(this).val());
        });
    }

    // --- Trang quan ly danh muc ---
    if ($('#categoryTableBody').length > 0) {
        loadCategoryTable(0);
        $('#btnOpenAddCategory').on('click', openAddCategory);
        $('#btnSaveCategory').on('click', saveCategory);
        $('#btnSearchCategory').on('click', function () { loadCategoryTable(0); });
        $('#categoryKeyword').on('keypress', function (e) {
            if (e.which === 13) loadCategoryTable(0);
        });
        $('#categoryTableBody').on('click', '.btn-edit-category', function () {
            openEditCategory($(this).data('id'));
        });
        $('#categoryTableBody').on('click', '.btn-delete-category', function () {
            deleteCategory($(this).data('id'));
        });
        $('#categoryPagination').on('click', '.page-link-category', function (e) {
            e.preventDefault();
            var page = $(this).data('page');
            if (page >= 0) loadCategoryTable(page);
        });
    }

    // --- Trang quan ly san pham ---
    if ($('#productTableBody').length > 0) {
        loadProductTable(0);
        $('#btnOpenAddProduct').on('click', openAddProduct);
        $('#btnSaveProduct').on('click', saveProduct);
        $('#btnSearchProduct').on('click', function () { loadProductTable(0); });
        $('#productKeyword').on('keypress', function (e) {
            if (e.which === 13) loadProductTable(0);
        });
        $('#productPageSize').on('change', function () { loadProductTable(0); });
        $('#productTableBody').on('click', '.btn-edit-product', function () {
            openEditProduct($(this).data('id'));
        });
        $('#productTableBody').on('click', '.btn-delete-product', function () {
            deleteProduct($(this).data('id'));
        });
        $('#productPagination').on('click', '.page-link-product', function (e) {
            e.preventDefault();
            var page = $(this).data('page');
            if (page >= 0) loadProductTable(page);
        });
    }
});
