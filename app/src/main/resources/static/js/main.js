import { productAPI, cartAPI } from './api.js';

// رویداد بارگذاری صفحه
document.addEventListener('DOMContentLoaded', () => {
    // اگر ظرف محصولات در صفحه باشد، محصولات را بارگذاری کن
    const productsContainer = document.getElementById('products-container');
    if (productsContainer) {
        loadProductsForHome(productsContainer);
    }
    // همیشه آیکون سبد خرید را به‌روز کن
    updateCartIcon();
});

// تابع بارگذاری محصولات در صفحه اصلی
async function loadProductsForHome(container) {
    try {
        showLoading();
        const products = await productAPI.getAllProducts();
        hideLoading();
        container.innerHTML = products.map(product => `
            <div class="col-md-4 mb-4">
                <div class="card h-100">
                    <img src="${product.image || 'https://via.placeholder.com/300'}" class="card-img-top" alt="${product.name}">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title">${product.name}</h5>
                        <p class="card-text flex-grow-1">${product.description.substring(0, 100)}...</p>
                        <p class="card-text"><strong>قیمت: ${product.price} تومان</strong></p>
                        <button class="btn btn-primary mt-auto" onclick="addToCart('${product._id}')">افزودن به سبد خرید</button>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (error) {
        hideLoading();
        console.error('Error loading products:', error);
        container.innerHTML = '<p>خطا در بارگذاری محصولات!</p>';
    }
}

// تابع افزودن به سبد خرید
window.addToCart = async function(productId) {
    try {
        const product = await productAPI.getProductById(productId);
        cartAPI.addToCart(product);
        showToast('محصول به سبد خرید اضافه شد!', 'success');
        updateCartIcon();
    } catch (error) {
        console.error('Error adding to cart:', error);
        showToast('خطا در افزودن محصول به سبد خرید!', 'error');
    }
};

// به‌روزرسانی آیکون سبد خرید
function updateCartIcon() {
    const cart = cartAPI.getCart();
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    const cartIcon = document.querySelector('.cart-icon');
    if (cartIcon) {
        const badge = cartIcon.nextElementSibling;
        if (badge) badge.textContent = totalItems;
    }
}

// نمایش لودینگ
function showLoading() {
    const spinner = document.querySelector('.loading-spinner');
    if (spinner) spinner.classList.add('active');
}

// مخفی کردن لودینگ
function hideLoading() {
    const spinner = document.querySelector('.loading-spinner');
    if (spinner) spinner.classList.remove('active');
}

// نمایش Toast پیام
function showToast(message, type = 'success') {
    const toastEl = document.getElementById('liveToast');
    if (!toastEl) return;
    const toastBody = toastEl.querySelector('.toast-body');
    toastBody.textContent = message;
    toastEl.className = `toast bg-${type === 'success' ? 'success' : 'danger'} text-white`;
    const toast = new bootstrap.Toast(toastEl);
    toast.show();
}

// اکسپورت توابع مورد نیاز برای سایر فایل‌ها
window.updateCartIcon = updateCartIcon;
window.showToast = showToast;
window.showLoading = showLoading;
window.hideLoading = hideLoading;
