import { productAPI, cartAPI } from './api.js';


document.addEventListener('DOMContentLoaded', async () => {
    // استخراج ID از URL
    const params = new URLSearchParams(window.location.search);
    const productId = params.get('id');

    if (!productId) {
        document.getElementById('product-detail').innerHTML = '<p>محصول یافت نشد!</p>';
        return;
    }

    // دریافت اطلاعات محصول از API
    const product = await productAPI.getProductById(productId);

    if (!product) {
        document.getElementById('product-detail').innerHTML = '<p>محصول یافت نشد!</p>';
        return;
    }

    // نمایش اطلاعات محصول
    document.getElementById('product-detail').innerHTML = `
        <div class="col-md-6">
            <img src="${product.image || 'https://via.placeholder.com/600'}" class="img-fluid rounded" alt="${product.name}">
        </div>
        <div class="col-md-6">
            <h1>${product.name}</h1>
            <p class="text-muted">${product.category}</p>
            <p>${product.description}</p>
            <h3 class="text-primary">${product.price} تومان</h3>
            <button class="btn btn-primary btn-lg mt-3" onclick="addToCart('${product._id}')">
                <i class="fas fa-shopping-cart"></i> افزودن به سبد خرید
            </button>
        </div>
    `;
});


function addToCart(productId) {
    // دریافت اطلاعات محصول
    productAPI.getProductById(productId).then(product => {
        cartAPI.addToCart(product);
        alert('محصول به سبد خرید اضافه شد!');

        // به‌روزرسانی آیکون سبد خرید (اگر وجود دارد)
        updateCartIcon();
    });
}

// تابع به‌روزرسانی آیکون سبد خرید
function updateCartIcon() {
    const cart = cartAPI.getCart();
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);

    // اگر آیکون سبد خرید در ناوبر وجود دارد
    const cartIcon = document.querySelector('.cart-icon');
    if (cartIcon) {
        cartIcon.setAttribute('data-count', totalItems);
    }
}