document.addEventListener('DOMContentLoaded', function () {
    // Khởi tạo tooltip
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Xử lý modal cập nhật nhân viên
    var updateModal = document.getElementById('updateStaffModal');
    if (updateModal) {
        updateModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            var staffId = button.getAttribute('data-staff-id');
            var staffCode = button.getAttribute('data-staff-code');
            var staffName = button.getAttribute('data-staff-name');
            var accountFe = button.getAttribute('data-staff-account-fe');
            var accountFpt = button.getAttribute('data-staff-account-fpt');
            var status = button.getAttribute('data-staff-status');

            // Cập nhật action của form
            var form = document.getElementById('updateStaffForm');
            form.action = '/staff/update/' + staffId;

            // Điền giá trị vào form
            document.getElementById('updateStaffId').value = staffId;
            document.getElementById('updateStaffCode').value = staffCode;
            document.getElementById('updateName').value = staffName;
            document.getElementById('updateAccountFe').value = accountFe;
            document.getElementById('updateAccountFpt').value = accountFpt;
            document.getElementById('updateStatus').value = status;
        });
    }

    // Xử lý form tạo nhân viên
    const createForm = document.getElementById('createStaffForm');
    if (createForm) {
        createForm.addEventListener('submit', function(e) {
            e.preventDefault();
            submitFormWithAjax(createForm, 'create');
        });
    }

    // Xử lý form cập nhật nhân viên
    const updateForm = document.getElementById('updateStaffForm');
    if (updateForm) {
        updateForm.addEventListener('submit', function(e) {
            e.preventDefault();
            submitFormWithAjax(updateForm, 'update');
        });
    }

    // Hàm gửi form qua AJAX
    function submitFormWithAjax(form, formType) {
        const formData = new FormData(form);
        const formErrorElement = document.getElementById(formType + 'FormError');

        fetch(form.action, {
            method: 'POST',
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Đóng modal
                    var modal = bootstrap.Modal.getInstance(document.getElementById(formType + 'StaffModal'));
                    modal.hide();

                    // Tạo toast mới để hiển thị thông báo thành công
                    const flashToast = document.createElement('div');
                    flashToast.className = 'toast';
                    flashToast.role = 'alert';
                    flashToast.setAttribute('aria-live', 'assertive');
                    flashToast.setAttribute('aria-atomic', 'true');
                    flashToast.setAttribute('data-bs-autohide', 'true');
                    flashToast.setAttribute('data-bs-delay', '3000');

                    const flashToastHeader = document.createElement('div');
                    flashToastHeader.className = 'toast-header bg-success text-white';
                    flashToastHeader.innerHTML = `
                    <strong class="me-auto">Thông báo</strong>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
                `;

                    const flashToastBody = document.createElement('div');
                    flashToastBody.className = 'toast-body';
                    flashToastBody.textContent = data.message;

                    flashToast.appendChild(flashToastHeader);
                    flashToast.appendChild(flashToastBody);

                    // Thêm toast vào container
                    document.querySelector('.position-fixed.top-0.start-0').appendChild(flashToast);

                    // Hiển thị toast
                    const bsFlashToast = new bootstrap.Toast(flashToast);
                    bsFlashToast.show();

                    // Làm mới trang sau một khoảng thời gian ngắn
                    setTimeout(() => {
                        window.location.reload();
                    }, 1000);
                } else {
                    // Hiển thị lỗi xác thực
                    if (data.fieldErrors) {
                        for (const [field, errors] of Object.entries(data.fieldErrors)) {
                            const errorElement = document.getElementById(formType + field.charAt(0).toUpperCase() + field.slice(1) + 'Error');
                            if (errorElement) {
                                errorElement.textContent = errors.join(', ');
                            }
                        }
                    }

                    // Hiển thị lỗi chung nếu có
                    if (data.message) {
                        formErrorElement.textContent = data.message;
                        formErrorElement.classList.remove('d-none');
                    }
                }
            })
            .catch(error => {
                console.error('Error:', error);
                formErrorElement.textContent = 'Có lỗi xảy ra. Vui lòng thử lại sau.';
                formErrorElement.classList.remove('d-none');
            });
    }

    // Xử lý toast xác nhận đổi trạng thái
    const toggleToast = document.getElementById('toggleToast');
    const confirmToastButton = document.getElementById('confirmToastButton');
    const toastMessage = document.getElementById('toastMessage');
    let currentStaffId = null;
    let currentAction = null;

    document.querySelectorAll('.btn-toggle').forEach(button => {
        button.addEventListener('click', function () {
            currentStaffId = this.getAttribute('data-staff-id');
            currentAction = this.getAttribute('data-action');

            // Cập nhật nội dung toast dựa trên hành động
            const toastHeader = toggleToast.querySelector('.toast-header');
            const confirmBtn = toggleToast.querySelector('#confirmToastButton');
            if (currentAction === 'deactivate') {
                toastMessage.textContent = 'Bạn có chắc muốn ngừng hoạt động nhân viên này?';
                toastHeader.classList.add('deactivate');
                confirmBtn.classList.add('deactivate');
            } else {
                toastMessage.textContent = 'Bạn có chắc muốn kích hoạt nhân viên này?';
                toastHeader.classList.remove('deactivate');
                confirmBtn.classList.remove('deactivate');
            }

            // Hiển thị toast
            const bsToast = new bootstrap.Toast(toggleToast, { autohide: false });
            bsToast.show();
        });
    });

    // Xử lý khi nhấn nút xác nhận trong toast
    confirmToastButton.addEventListener('click', function () {
        if (currentStaffId) {
            fetch(`/staff/api/toggle-status/${currentStaffId}`, {
                method: 'POST',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            })
                .then(response => response.json())
                .then(data => {
                    const bsToast = bootstrap.Toast.getInstance(toggleToast);
                    bsToast.hide();

                    // Tạo toast mới để hiển thị thông báo thành công hoặc lỗi
                    const flashToast = document.createElement('div');
                    flashToast.className = 'toast';
                    flashToast.role = 'alert';
                    flashToast.setAttribute('aria-live', 'assertive');
                    flashToast.setAttribute('aria-atomic', 'true');
                    flashToast.setAttribute('data-bs-autohide', 'true');
                    flashToast.setAttribute('data-bs-delay', '3000');

                    const flashToastHeader = document.createElement('div');
                    flashToastHeader.className = `toast-header ${data.success ? 'bg-success' : 'bg-danger'} text-white`;
                    flashToastHeader.innerHTML = `
                    <strong class="me-auto">Thông báo</strong>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
                `;

                    const flashToastBody = document.createElement('div');
                    flashToastBody.className = 'toast-body';
                    flashToastBody.textContent = data.message;

                    flashToast.appendChild(flashToastHeader);
                    flashToast.appendChild(flashToastBody);

                    // Thêm toast vào container
                    document.querySelector('.position-fixed.top-0.start-0').appendChild(flashToast);

                    // Hiển thị toast
                    const bsFlashToast = new bootstrap.Toast(flashToast);
                    bsFlashToast.show();

                    if (data.success) {
                        setTimeout(() => {
                            window.location.reload();
                        }, 1000);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    const bsToast = bootstrap.Toast.getInstance(toggleToast);
                    bsToast.hide();

                    // Tạo toast mới để hiển thị lỗi
                    const flashToast = document.createElement('div');
                    flashToast.className = 'toast';
                    flashToast.role = 'alert';
                    flashToast.setAttribute('aria-live', 'assertive');
                    flashToast.setAttribute('aria-atomic', 'true');
                    flashToast.setAttribute('data-bs-autohide', 'true');
                    flashToast.setAttribute('data-bs-delay', '3000');

                    const flashToastHeader = document.createElement('div');
                    flashToastHeader.className = 'toast-header bg-danger text-white';
                    flashToastHeader.innerHTML = `
                    <strong class="me-auto">Thông báo</strong>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
                `;

                    const flashToastBody = document.createElement('div');
                    flashToastBody.className = 'toast-body';
                    flashToastBody.textContent = 'Có lỗi xảy ra. Vui lòng thử lại sau.';

                    flashToast.appendChild(flashToastHeader);
                    flashToast.appendChild(flashToastBody);

                    // Thêm toast vào container
                    document.querySelector('.position-fixed.top-0.start-0').appendChild(flashToast);

                    // Hiển thị toast
                    const bsFlashToast = new bootstrap.Toast(flashToast);
                    bsFlashToast.show();
                });
        }
    });

    // Đặt lại trạng thái toast khi đóng
    toggleToast.addEventListener('hidden.bs.toast', function () {
        currentStaffId = null;
        currentAction = null;
        const toastHeader = toggleToast.querySelector('.toast-header');
        const confirmBtn = toggleToast.querySelector('#confirmToastButton');
        toastHeader.classList.remove('deactivate');
        confirmBtn.classList.remove('deactivate');
    });

    // Xử lý bộ lọc: tự động gửi form khi thay đổi ô tìm kiếm hoặc trạng thái
    const filterForm = document.getElementById('filterForm');
    const searchInput = document.getElementById('search');
    const statusSelect = document.getElementById('status');

    // Gửi form khi thay đổi ô tìm kiếm (sau 500ms kể từ lần gõ cuối)
    let searchTimeout;
    searchInput.addEventListener('input', function () {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            filterForm.submit();
        }, 500);
    });

    // Gửi form ngay khi thay đổi trạng thái
    statusSelect.addEventListener('change', function () {
        filterForm.submit();
    });
});