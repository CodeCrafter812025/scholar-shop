import { productAPI, userAPI, orderAPI } from './api.js';

document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');

    if (!token) {
        alert('لطفاً وارد شوید!');
        window.location.href = 'auth.html';
        return;
    }

    // بارگذاری محصولات
    await loadProducts();

    // بارگذاری کاربران
    await loadUsers();

    // بارگذاری سفارش‌ها
    await loadOrders();

    // مدیریت فرم افزودن محصول
    document.getElementById('addProductForm')?.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(e.target);
        const productData = Object.fromEntries(formData);

        try {
            await productAPI.createProduct(productData, token);
            alert('محصول با موفقیت افزوده شد!');
            e.target.reset();
            bootstrap.Modal.getInstance(document.getElementById('addProductModal')).hide();
            await loadProducts();
        } catch (error) {
            alert('خطا در افزودن محصول!');
        }
    });
});

async function loadProducts() {
    const productsList = document.getElementById('products-list');
    try {
        const products = await productAPI.getAllProducts();
        productsList.innerHTML = products.map(product => `
            <div class="card mb-3">
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-2">
                            <img src="${product.image || 'https://via.placeholder.com/100'}" class="img-fluid">
                        </div>
                        <div class="col-md-8">
                            <h5>${product.name}</h5>
                            <p>${product.description.substring(0, 100)}...</p>
                            <p><strong>قیمت: ${product.price} تومان</strong></p>
                        </div>
                        <div class="col-md-2">
                            <button class="btn btn-warning btn-sm mb-2">ویرایش</button>
                            <button class="btn btn-danger btn-sm" onclick="deleteProduct('${product._id}')">حذف</button>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (error) {
        productsList.innerHTML = '<p>خطا در بارگذاری محصولات!</p>';
    }
}

async function loadUsers() {
    const usersList = document.getElementById('users-list');
    // مشابه loadProducts پیاده‌سازی شود
}

async function loadOrders() {
    const ordersList = document.getElementById('orders-list');
    // مشابه loadProducts پیاده‌سازی شود
}

async function deleteProduct(productId) {
    if (confirm('آیا از حذف این محصول مطمئن هستید؟')) {
        const token = localStorage.getItem('token');
        try {
            await productAPI.deleteProduct(productId, token);
            alert('محصول با موفقیت حذف شد!');
            await loadProducts();
        } catch (error) {
            alert('خطا در حذف محصول!');
        }
    }
}