import { productAPI, cartAPI } from './api.js';

document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    const detailContainer = document.getElementById('product-detail');

    if (!productId) {
        detailContainer.innerHTML = '<p>محصولی انتخاب نشده است.</p>';
        return;
    }

    try {
        const product = await productAPI.getProductById(productId);

        detailContainer.innerHTML = `
            <div class="col-md-6">
                <img src="${product.image?.path || product.image || 'https://via.placeholder.com/600'}"
                     class="img-fluid rounded" alt="${product.title || product.name}">
            </div>
            <div class="col-md-6">
                <h1>${product.title || product.name}</h1>
                <p class="text-muted">${product.category?.name || product.category?.title || ''}</p>
                <p>${product.description || ''}</p>
                <h3 class="text-primary">${product.price} تومان</h3>
                <button class="btn btn-primary btn-lg mt-3" onclick="addToCart('${product.id || product._id}')">
                    <i class="fas fa-shopping-cart"></i> افزودن به سبد خرید
                </button>
            </div>
        `;
    } catch (error) {
        console.error('Error fetching product detail:', error);
        detailContainer.innerHTML = '<p>خطا در دریافت اطلاعات محصول.</p>';
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
