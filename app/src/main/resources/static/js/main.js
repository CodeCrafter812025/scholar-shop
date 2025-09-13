import { productAPI, cartAPI } from './api.js';

document.addEventListener('DOMContentLoaded', async () => {
    const productsContainer = document.getElementById('products-container');
    if (productsContainer) {
        loadProductsForHome(productsContainer);
    }
    updateCartIcon();
});

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
});

// تابع افزودن به سبد خرید
window.addToCart = async function(productId) {
    try {
        const product = await productAPI.getProductById(productId);
        cartAPI.addToCart(product);
        alert('محصول به سبد خرید اضافه شد!');
        updateCartIcon();
    } catch (error) {
        console.error('Error adding to cart:', error);
        alert('خطا در افزودن محصول به سبد خرید!');
    }
};

// تابع به‌روزرسانی آیکون سبد خرید
function updateCartIcon() {
    const cart = cartAPI.getCart();
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);

    const cartIcon = document.querySelector('.cart-icon');
    if (cartIcon) {
        const badge = cartIcon.nextElementSibling;
        if (badge) {
            badge.textContent = totalItems;
        }
    }
}

// نمایش لودینگ
function showLoading() {
    document.querySelector('.loading-spinner').classList.add('active');
}

// مخفی کردن لودینگ
function hideLoading() {
    document.querySelector('.loading-spinner').classList.remove('active');
}

// مثال استفاده
showLoading();
const products = await productAPI.getAllProducts();
hideLoading();


function showToast(message, type = 'success') {
    const toastEl = document.getElementById('liveToast');
    const toastBody = toastEl.querySelector('.toast-body');

    toastBody.textContent = message;
    toastEl.className = `toast bg-${type === 'success' ? 'success' : 'danger'} text-white`;

    const toast = new bootstrap.Toast(toastEl);
    toast.show();
}

// مثال استفاده
showToast('محصول به سبد خرید اضافه شد!');
