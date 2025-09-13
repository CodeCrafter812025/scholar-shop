import { productAPI, cartAPI } from './api.js';

// رویداد بارگذاری صفحه
document.addEventListener('DOMContentLoaded', () => {
    const productsContainer = document.getElementById('products-container');
    if (productsContainer) {
        loadProductsForHome(productsContainer);
    }
    updateCartIcon();
});

// بارگذاری محصولات صفحه‌ی اصلی
async function loadProductsForHome(container) {
    try {
        showLoading();
        const products = await productAPI.getAllProducts(0, 12);
        hideLoading();
        container.innerHTML = products.map(product => {
            const imagePath = product.image?.path || product.image || 'https://via.placeholder.com/300';
            const title = product.title || product.name || '';
            const desc = product.description || '';
            const id = product.id || product._id;
            return `
                <div class="col-md-4 mb-4">
                    <div class="card h-100">
                        <img src="${imagePath}" class="card-img-top" alt="${title}">
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title">${title}</h5>
                            <p class="card-text flex-grow-1">${desc.substring(0, 100)}...</p>
                            <p class="card-text"><strong>قیمت: ${product.price} تومان</strong></p>
                            <button class="btn btn-primary mt-auto" onclick="addToCart('${id}')">افزودن به سبد خرید</button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
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

// به‌روزرسانی نمایش تعداد سبد
function updateCartIcon() {
    const cart = cartAPI.getCart();
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    const cartIcon = document.querySelector('.cart-icon');
    if (cartIcon) {
        const badge = cartIcon.nextElementSibling;
        if (badge) badge.textContent = totalItems;
    }
}

// نمایش و پنهان کردن لودینگ
function showLoading() {
    const spinner = document.querySelector('.loading-spinner');
    if (spinner) spinner.classList.add('active');
}

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

// اکسپورت توابعی که نیاز است global باشند
window.updateCartIcon = updateCartIcon;
window.showToast = showToast;
window.showLoading = showLoading;
window.hideLoading = hideLoading;
