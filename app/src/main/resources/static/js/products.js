import { productAPI, cartAPI } from './api.js';

document.addEventListener('DOMContentLoaded', async () => {
    const productsContainer = document.getElementById('products-container');

    // دریافت محصولات از API
    const products = await productAPI.getAllProducts();

    // نمایش محصولات در صفحه
    productsContainer.innerHTML = products.map(product => `
        <div class="col-md-4 mb-4">
            <div class="card">
                <img src="${product.image || 'https://via.placeholder.com/300'}" class="card-img-top" alt="${product.name}">
                <div class="card-body">
                    <h5 class="card-title">${product.name}</h5>
                    <p class="card-text">${product.description.substring(0, 100)}...</p>
                    <p class="card-text"><strong>قیمت: ${product.price} تومان</strong></p>
                    <button class="btn btn-primary" onclick="addToCart('${product._id}')">افزودن به سبد خرید</button>
                    <a href="/product-detail.html?id=${product._id}" class="btn btn-outline-primary">جزئیات</a>
                </div>
            </div>
        </div>
    `).join('');
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