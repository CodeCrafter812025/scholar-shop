import { productAPI, cartAPI } from './api.js';


// پس از دریافت product از API ...
document.getElementById('product-detail').innerHTML = `
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