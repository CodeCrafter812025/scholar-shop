import { productAPI, userAPI, orderAPI } from './api.js';

// نمایش محصولات پنل ادمین
async function loadProducts() {
    const productsList = document.getElementById('products-list');
    productsList.innerHTML = '<p>در حال بارگذاری محصولات...</p>';
    try {
        const token = localStorage.getItem('token');
        const products = await productAPI.getAllPanelProducts(0, 100, token);
        if (!products || products.length === 0) {
            productsList.innerHTML = '<p>محصولی یافت نشد.</p>';
            return;
        }
        productsList.innerHTML = products.map(product => {
            // تعیین مسیر تصویر
            const imagePath = product.image?.path
                ? product.image.path
                : product.image?.name
                    ? `/images/${product.image.name}`
                    : product.image
                        ? product.image
                        : 'https://via.placeholder.com/100';
            const title = product.title || product.name || '';
            return `
                <div class="card mb-3">
                    <div class="row g-0">
                        <div class="col-md-2">
                            <img src="${imagePath}" class="img-fluid rounded-start" alt="${title}">
                        </div>
                        <div class="col-md-8">
                            <div class="card-body">
                                <h5 class="card-title">${title}</h5>
                                <p class="card-text"><strong>قیمت:</strong> ${product.price} تومان</p>
                            </div>
                        </div>
                        <div class="col-md-2 d-flex flex-column justify-content-center align-items-center">
                            <button class="btn btn-danger mb-2" onclick="deleteProduct('${product.id || product._id}')">حذف</button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    } catch (error) {
        productsList.innerHTML = '<p>خطا در بارگذاری محصولات!</p>';
        console.error('loadProducts', error);
    }
}

// افزودن محصول جدید
document.getElementById('saveProductBtn')?.addEventListener('click', async (e) => {
    e.preventDefault();
    const name = document.getElementById('productName')?.value.trim();
    const price = document.getElementById('productPrice')?.value.trim();
    const description = document.getElementById('productDescription')?.value.trim();
    const image = document.getElementById('productImageUrl')?.value.trim();
    if (!name || !price) {
        alert('لطفاً نام و قیمت را وارد کنید.');
        return;
    }
    try {
        const token = localStorage.getItem('token');
        // توجه: اگر category یا فیلدهای دیگر لازم است، آن‌ها را نیز اضافه کنید
        await productAPI.createProduct(
            { title: name, price: parseInt(price), description, image },
            token
        );
        alert('محصول جدید با موفقیت ایجاد شد');
        // بارگذاری مجدد لیست
        loadProducts();
        // پاک‌سازی فرم (اختیاری)
        document.getElementById('productName').value = '';
        document.getElementById('productPrice').value = '';
        document.getElementById('productDescription').value = '';
        document.getElementById('productImageUrl').value = '';
        // بستن مودال در صورت استفاده از Bootstrap Modal
        const modalEl = document.getElementById('addProductModal');
        const modal = bootstrap.Modal.getInstance(modalEl);
        modal?.hide();
    } catch (error) {
        console.error('createProduct', error);
        alert('خطا در ایجاد محصول!');
    }
});

// حذف محصول
window.deleteProduct = async function(productId) {
    if (!confirm('آیا از حذف این محصول مطمئن هستید؟')) return;
    try {
        const token = localStorage.getItem('token');
        await productAPI.deleteProduct(productId, token);
        alert('محصول با موفقیت حذف شد');
    } catch (error) {
        console.error('deleteProduct', error);
        alert('خطا در حذف محصول!');
    } finally {
        // چه موفق و چه ناموفق، لیست را بارگذاری مجدد می‌کنیم
        loadProducts();
    }
};

// بارگذاری کاربران، سفارش‌ها و گزارشات مشابه نسخه قبلی...

document.addEventListener('DOMContentLoaded', async () => {
    await loadProducts();
    await loadUsers();
    await loadOrders();
    await loadReports();
});
