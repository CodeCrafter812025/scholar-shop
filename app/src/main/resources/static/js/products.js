import { productAPI, cartAPI } from './api.js';

document.addEventListener('DOMContentLoaded', async () => {
    const productsContainer = document.getElementById('products-container');

    // دریافت محصولات (page=0 و size=100)
    const products = await productAPI.getAllProducts(0, 100);

    // نمایش محصولات
    productsContainer.innerHTML = products.map(product => {
        const imagePath = product.image?.path || product.image || 'https://via.placeholder.com/300';
        const title = product.title || product.name || '';
        const desc = product.description || '';
        const id = product.id || product._id;
        return `
            <div class="col-md-4 mb-4">
                <div class="card">
                    <img src="${imagePath}" class="card-img-top" alt="${title}">
                    <div class="card-body">
                        <h5 class="card-title">${title}</h5>
                        <p class="card-text">${desc.substring(0, 100)}...</p>
                        <p class="card-text"><strong>قیمت: ${product.price} تومان</strong></p>
                        <button class="btn btn-primary" onclick="addToCart('${id}')">افزودن به سبد خرید</button>
                        <a href="/product-detail.html?id=${id}" class="btn btn-outline-primary">جزئیات</a>
                    </div>
                </div>
            </div>
        `;
    }).join('');
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
