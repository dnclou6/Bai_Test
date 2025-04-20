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

                    // Hiển thị thông báo thành công và làm mới trang
                    const flashMessage = document.createElement('div');
                    flashMessage.className = 'alert alert-success flash-message';
                    flashMessage.role = 'alert';
                    flashMessage.textContent = data.message;
                    document.body.insertBefore(flashMessage, document.querySelector('.table-container'));

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

    // Xử lý modal xác nhận đổi trạng thái
    const confirmModal = document.getElementById('confirmToggleModal');
    const confirmButton = document.getElementById('confirmToggleButton');
    const confirmMessage = document.getElementById('confirmMessage');
    const modalBody = confirmModal.querySelector('.modal-body');
    const modalHeader = confirmModal.querySelector('.modal-header');
    const confirmBtn = confirmModal.querySelector('.btn-confirm');
    let toggleForm = null;

    // Xử lý khi nút toggle được nhấn
    document.querySelectorAll('.btn-toggle').forEach(button => {
        button.addEventListener('click', function () {
            const staffId = this.getAttribute('data-staff-id');
            const action = this.getAttribute('data-action');
            toggleForm = this.closest('form');

            // Cập nhật nội dung modal dựa trên hành động
            if (action === 'deactivate') {
                confirmMessage.textContent = 'Bạn có chắc muốn ngừng hoạt động nhân viên này?';
                modalBody.classList.add('deactivate');
                modalHeader.classList.add('deactivate');
                confirmBtn.classList.add('deactivate');
            } else {
                confirmMessage.textContent = 'Bạn có chắc muốn kích hoạt nhân viên này?';
                modalBody.classList.remove('deactivate');
                modalHeader.classList.remove('deactivate');
                confirmBtn.classList.remove('deactivate');
            }
        });
    });

    // Xử lý khi nhấn nút xác nhận
    confirmButton.addEventListener('click', function () {
        if (toggleForm) {
            const staffId = toggleForm.querySelector('.btn-toggle').getAttribute('data-staff-id');
            fetch(`/staff/api/toggle-status/${staffId}`, {
                method: 'POST',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            })
                .then(response => response.json())
                .then(data => {
                    const modal = bootstrap.Modal.getInstance(confirmModal);
                    modal.hide();

                    const flashMessage = document.createElement('div');
                    flashMessage.className = `alert ${data.success ? 'alert-success' : 'alert-danger'} flash-message`;
                    flashMessage.role = 'alert';
                    flashMessage.textContent = data.message;
                    document.body.insertBefore(flashMessage, document.querySelector('.table-container'));

                    if (data.success) {
                        setTimeout(() => {
                            window.location.reload();
                        }, 1000);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    const modal = bootstrap.Modal.getInstance(confirmModal);
                    modal.hide();

                    const flashMessage = document.createElement('div');
                    flashMessage.className = 'alert alert-danger flash-message';
                    flashMessage.role = 'alert';
                    flashMessage.textContent = 'Có lỗi xảy ra. Vui lòng thử lại sau.';
                    document.body.insertBefore(flashMessage, document.querySelector('.table-container'));
                });
        }
    });

    // Đặt lại trạng thái modal khi đóng
    confirmModal.addEventListener('hidden.bs.modal', function () {
        modalBody.classList.remove('deactivate');
        modalHeader.classList.remove('deactivate');
        confirmBtn.classList.remove('deactivate');
        toggleForm = null;
    });
});