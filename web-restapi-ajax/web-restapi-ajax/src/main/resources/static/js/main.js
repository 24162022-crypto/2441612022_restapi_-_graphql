/*
 * File: src/main/resources/static/js/main.js
 * Toan bo xu ly AJAX cho trang Category va Product.
 * Yeu cau: jQuery 3.7.1 va bien contextPath da duoc khai bao o layout.
 */

// ============ HAM TIEN ICH DUNG CHUNG ============

/** Hien thong bao tren dau trang */
function showAlert(boxId, message, isSuccess) {
    var $box = $('#' + boxId);
    $box.removeClass('d-none alert-success alert-danger');
    $box.addClass(isSuccess ? 'alert-success' : 'alert-danger');
    $box.text(message);
    // Tu an sau 4 giay
    setTimeout(function () {
        $box.addClass('d-none');
    }, 4000);
}

/** Doc thong bao loi tu response cua server, khong bao gio de trong */
function parseError(jqXHR, textStatus, errorThrown) {
    if (jqXHR.responseJSON && jqXHR.responseJSON.message) {
        return jqXHR.responseJSON.message;
    }
    if (jqXHR.status === 0) {
        return 'Khong ket noi duoc toi server. Kiem tra server da chay chua.';
    }
    if (jqXHR.status === 415) {
        return 'Loi 415: sai kieu du lieu gui len. Kiem tra contentType va processData.';
    }
    if (jqXHR.status === 404) {
        return 'Loi 404: khong tim thay duong dan API.';
    }
    return 'Loi ' + jqXHR.status + ': ' + (errorThrown || textStatus);
}

/** Dinh dang tien te VND */
function formatMoney(value) {
    if (value === null || value === undefined) return '';
    return Number(value).toLocaleString('vi-VN') + ' d';
}

/** Chong XSS khi do du lieu vao HTML */
function escapeHtml(text) {
    if (text === null || text === undefined) return '';
    return $('<div>').text(text).html();
}

// ============ PHAN CATEGORY ============

/** Tai danh sach danh muc va render ra bang */
function loadCategories() {
    $.getJSON(contextPath + '/api/category', function (res) {
        var html = '';
        var list = res.body || [];
        if (list.length === 0) {
            html = '<tr><td colspan="4" class="text-center">Chua co danh muc nao</td></tr>';
        }
        $.each(list, function (i, c) {
            var iconHtml = c.icon
                ? '<img src="' + contextPath + '/admin/categories/images/' + c.icon
                    + '" width="50" height="50" style="object-fit:cover">'
                : '<span class="text-muted">Khong co</span>';
            html += '<tr>'
                + '<td>' + c.categoryId + '</td>'
                + '<td>' + escapeHtml(c.categoryName) + '</td>'
                + '<td>' + iconHtml + '</td>'
                + '<td>'
                + '  <button class="btn btn-sm btn-warning btn-edit-category" data-id="' + c.categoryId + '">Sua</button> '
                + '  <button class="btn btn-sm btn-danger btn-delete-category" data-id="' + c.categoryId + '">Xoa</button>'
                + '</td>'
                + '</tr>';
        });
        $('#categoryTableBody').html(html);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        // BAT BUOC co error handler, khong de trong
        $('#categoryTableBody').html(
            '<tr><td colspan="4" class="text-danger text-center">'
            + escapeHtml(parseError(jqXHR, textStatus, errorThrown)) + '</td></tr>');
    });
}

/** Mo modal o che do them moi */
function openAddCategory() {
    $('#categoryModalTitle').text('Them danh muc');
    $('#categoryId').val('');
    $('#categoryName').val('');
    $('#categoryIcon').val('');
    $('#categoryModalError').text('');
    new bootstrap.Modal(document.getElementById('categoryModal')).show();
}

/** Mo modal o che do sua, do san du lieu cu */
function openEditCategory(id) {
    $.ajax({
        url: contextPath + '/api/category/getCategory',
        type: 'POST',
        data: { id: id }, // gui dang x-www-form-urlencoded, khop voi @RequestParam
        success: function (res) {
            if (!res.status) {
                showAlert('categoryAlert', res.message, false);
                return;
            }
            var c = res.body;
            $('#categoryModalTitle').text('Sua danh muc');
            $('#categoryId').val(c.categoryId);
            $('#categoryName').val(c.categoryName);
            $('#categoryIcon').val('');
            $('#categoryModalError').text('');
            new bootstrap.Modal(document.getElementById('categoryModal')).show();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            showAlert('categoryAlert', parseError(jqXHR, textStatus, errorThrown), false);
        }
    });
}

/** Luu danh muc (them moi hoac cap nhat) */
function saveCategory() {
    var id = $('#categoryId').val();
    var name = $.trim($('#categoryName').val());
    var iconInput = document.getElementById('categoryIcon');

    if (name === '') {
        $('#categoryModalError').text('Ten danh muc khong duoc de trong');
        return;
    }

    var formData = new FormData();
    formData.append('categoryName', name);
    if (iconInput.files.length > 0) {
        formData.append('icon', iconInput.files[0]);
    }

    var url;
    if (id === '') {
        url = contextPath + '/api/category/addCategory';
    } else {
        url = contextPath + '/api/category/updateCategory';
        formData.append('categoryId', id);
        // Gui POST kem _method=PUT: HiddenHttpMethodFilter se doi thanh PUT.
        // Ly do: Spring/Tomcat KHONG tu parse multipart cho request PUT, nen gui
        // PUT truc tiep se lam cac @RequestParam bi null.
        formData.append('_method', 'PUT');
    }

    $.ajax({
        url: url,
        type: 'POST',
        data: formData,
        contentType: false,  // de trinh duyet tu dat multipart/form-data kem boundary
        processData: false,  // khong cho jQuery bien FormData thanh chuoi
        cache: false,
        success: function (res) {
            if (res.status) {
                bootstrap.Modal.getInstance(document.getElementById('categoryModal')).hide();
                showAlert('categoryAlert', res.message, true);
                loadCategories();
            } else {
                $('#categoryModalError').text(res.message);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $('#categoryModalError').text(parseError(jqXHR, textStatus, errorThrown));
        }
    });
}

/** Xoa danh muc */
function deleteCategory(id) {
    if (!confirm('Ban chac chan muon xoa danh muc nay?')) {
        return;
    }
    $.ajax({
        url: contextPath + '/api/category/deleteCategory',
        type: 'DELETE',
        data: { categoryId: id }, // DELETE voi tham so dang query/form van hoat dong binh thuong
        success: function (res) {
            showAlert('categoryAlert', res.message, res.status);
            loadCategories();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            showAlert('categoryAlert', parseError(jqXHR, textStatus, errorThrown), false);
        }
    });
}

// ============ PHAN PRODUCT ============

var productCurrentPage = 0;

/** Do danh sach danh muc vao the select trong modal san pham */
function loadCategoryOptions(selectedId, callback) {
    $.getJSON(contextPath + '/api/category', function (res) {
        var html = '';
        $.each(res.body || [], function (i, c) {
            var sel = (selectedId && Number(selectedId) === Number(c.categoryId)) ? ' selected' : '';
            html += '<option value="' + c.categoryId + '"' + sel + '>'
                + escapeHtml(c.categoryName) + '</option>';
        });
        $('#productCategoryId').html(html);
        if (typeof callback === 'function') callback();
    }).fail(function (jqXHR, textStatus, errorThrown) {
        $('#productModalError').text(parseError(jqXHR, textStatus, errorThrown));
    });
}

/** Tai danh sach san pham co tim kiem + phan trang */
function loadProducts(page) {
    productCurrentPage = page || 0;
    var keyword = $.trim($('#productKeyword').val());
    var size = $('#productPageSize').val();

    $.getJSON(contextPath + '/api/product/search', {
        keyword: keyword,
        page: productCurrentPage,
        size: size
    }, function (res) {
        var data = res.body || {};
        var list = data.content || [];
        var html = '';

        if (list.length === 0) {
            html = '<tr><td colspan="9" class="text-center">Khong tim thay san pham nao</td></tr>';
        }
        $.each(list, function (i, p) {
            var img = p.images
                ? '<img src="' + contextPath + '/admin/products/images/' + p.images
                    + '" width="60" height="60" style="object-fit:cover">'
                : '<span class="text-muted">-</span>';
            var statusBadge = (p.status === 1)
                ? '<span class="badge bg-success">Dang ban</span>'
                : '<span class="badge bg-secondary">Ngung ban</span>';
            html += '<tr>'
                + '<td>' + p.productId + '</td>'
                + '<td>' + img + '</td>'
                + '<td>' + escapeHtml(p.productName) + '</td>'
                + '<td>' + formatMoney(p.unitPrice) + '</td>'
                + '<td>' + p.quantity + '</td>'
                + '<td>' + p.discount + '%</td>'
                + '<td>' + escapeHtml(p.categoryName) + '</td>'
                + '<td>' + statusBadge + '</td>'
                + '<td>'
                + '  <button class="btn btn-sm btn-warning btn-edit-product" data-id="' + p.productId + '">Sua</button> '
                + '  <button class="btn btn-sm btn-danger btn-delete-product" data-id="' + p.productId + '">Xoa</button>'
                + '</td>'
                + '</tr>';
        });
        $('#productTableBody').html(html);
        renderPagination(data.totalPages || 0, data.currentPage || 0);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        $('#productTableBody').html(
            '<tr><td colspan="9" class="text-danger text-center">'
            + escapeHtml(parseError(jqXHR, textStatus, errorThrown)) + '</td></tr>');
        $('#productPagination').empty();
    });
}

/** Ve thanh phan trang */
function renderPagination(totalPages, currentPage) {
    var html = '';
    if (totalPages <= 1) {
        $('#productPagination').html('');
        return;
    }
    var prevDisabled = (currentPage === 0) ? ' disabled' : '';
    html += '<li class="page-item' + prevDisabled + '">'
        + '<a class="page-link page-link-product" href="#" data-page="' + (currentPage - 1) + '">Truoc</a></li>';

    for (var i = 0; i < totalPages; i++) {
        var active = (i === currentPage) ? ' active' : '';
        html += '<li class="page-item' + active + '">'
            + '<a class="page-link page-link-product" href="#" data-page="' + i + '">' + (i + 1) + '</a></li>';
    }

    var nextDisabled = (currentPage >= totalPages - 1) ? ' disabled' : '';
    html += '<li class="page-item' + nextDisabled + '">'
        + '<a class="page-link page-link-product" href="#" data-page="' + (currentPage + 1) + '">Sau</a></li>';

    $('#productPagination').html(html);
}

/** Mo modal them san pham */
function openAddProduct() {
    $('#productModalTitle').text('Them san pham');
    $('#productId').val('');
    $('#productName').val('');
    $('#productUnitPrice').val(0);
    $('#productQuantity').val(0);
    $('#productDiscount').val(0);
    $('#productDescription').val('');
    $('#productImageFile').val('');
    $('#productStatus').val('1');
    $('#productModalError').text('');
    loadCategoryOptions(null, function () {
        new bootstrap.Modal(document.getElementById('productModal')).show();
    });
}

/** Mo modal sua san pham, do san du lieu */
function openEditProduct(id) {
    $.ajax({
        url: contextPath + '/api/product/getProduct',
        type: 'POST',
        data: { id: id },
        success: function (res) {
            if (!res.status) {
                showAlert('productAlert', res.message, false);
                return;
            }
            var p = res.body;
            $('#productModalTitle').text('Sua san pham');
            $('#productId').val(p.productId);
            $('#productName').val(p.productName);
            $('#productUnitPrice').val(p.unitPrice);
            $('#productQuantity').val(p.quantity);
            $('#productDiscount').val(p.discount);
            $('#productDescription').val(p.description);
            $('#productImageFile').val('');
            $('#productStatus').val(p.status);
            $('#productModalError').text('');
            loadCategoryOptions(p.categoryId, function () {
                new bootstrap.Modal(document.getElementById('productModal')).show();
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            showAlert('productAlert', parseError(jqXHR, textStatus, errorThrown), false);
        }
    });
}

/** Luu san pham (them moi hoac cap nhat) */
function saveProduct() {
    var id = $('#productId').val();
    var name = $.trim($('#productName').val());
    var description = $.trim($('#productDescription').val());
    var categoryId = $('#productCategoryId').val();
    var imageInput = document.getElementById('productImageFile');

    if (name === '') {
        $('#productModalError').text('Ten san pham khong duoc de trong');
        return;
    }
    if (!categoryId) {
        $('#productModalError').text('Chua chon danh muc. Hay tao danh muc truoc.');
        return;
    }
    if (description === '') {
        // Cot description trong DB la NOT NULL nen bat buoc nhap
        $('#productModalError').text('Mo ta khong duoc de trong');
        return;
    }

    var formData = new FormData();
    formData.append('productName', name);
    formData.append('unitPrice', $('#productUnitPrice').val() || 0);
    formData.append('quantity', $('#productQuantity').val() || 0);
    formData.append('discount', $('#productDiscount').val() || 0);
    formData.append('description', description);
    formData.append('categoryId', categoryId);
    formData.append('status', $('#productStatus').val());
    if (imageInput.files.length > 0) {
        formData.append('imageFile', imageInput.files[0]);
    }

    var url;
    if (id === '') {
        url = contextPath + '/api/product/addProduct';
    } else {
        url = contextPath + '/api/product/updateProduct';
        formData.append('productId', id);
        formData.append('_method', 'PUT'); // xem giai thich o saveCategory()
    }

    $.ajax({
        url: url,
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        cache: false,
        success: function (res) {
            if (res.status) {
                bootstrap.Modal.getInstance(document.getElementById('productModal')).hide();
                showAlert('productAlert', res.message, true);
                loadProducts(productCurrentPage);
            } else {
                $('#productModalError').text(res.message);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $('#productModalError').text(parseError(jqXHR, textStatus, errorThrown));
        }
    });
}

/** Xoa san pham */
function deleteProduct(id) {
    if (!confirm('Ban chac chan muon xoa san pham nay?')) {
        return;
    }
    $.ajax({
        url: contextPath + '/api/product/deleteProduct',
        type: 'DELETE',
        data: { productId: id },
        success: function (res) {
            showAlert('productAlert', res.message, res.status);
            loadProducts(productCurrentPage);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            showAlert('productAlert', parseError(jqXHR, textStatus, errorThrown), false);
        }
    });
}

// ============ GAN SU KIEN KHI TRANG SAN SANG ============
$(document).ready(function () {

    // --- Trang Category ---
    if ($('#categoryTableBody').length > 0) {
        loadCategories();
        $('#btnOpenAddCategory').on('click', openAddCategory);
        $('#btnSaveCategory').on('click', saveCategory);
        // Dung event delegation vi cac nut duoc tao dong sau khi AJAX tra ve
        $('#categoryTableBody').on('click', '.btn-edit-category', function () {
            openEditCategory($(this).data('id'));
        });
        $('#categoryTableBody').on('click', '.btn-delete-category', function () {
            deleteCategory($(this).data('id'));
        });
    }

    // --- Trang Product ---
    if ($('#productTableBody').length > 0) {
        loadProducts(0);
        $('#btnOpenAddProduct').on('click', openAddProduct);
        $('#btnSaveProduct').on('click', saveProduct);
        $('#btnSearchProduct').on('click', function () {
            loadProducts(0);
        });
        $('#productKeyword').on('keypress', function (e) {
            if (e.which === 13) loadProducts(0);
        });
        $('#productPageSize').on('change', function () {
            loadProducts(0);
        });
        $('#productTableBody').on('click', '.btn-edit-product', function () {
            openEditProduct($(this).data('id'));
        });
        $('#productTableBody').on('click', '.btn-delete-product', function () {
            deleteProduct($(this).data('id'));
        });
        $('#productPagination').on('click', '.page-link-product', function (e) {
            e.preventDefault();
            var page = $(this).data('page');
            if (page >= 0) loadProducts(page);
        });
    }
});
