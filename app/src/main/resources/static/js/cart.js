import { cartAPI, orderAPI } from './api.js';

document.addEventListener('DOMContentLoaded', () => {
    const cartItemsContainer = document.getElementById('cart-items');
    const cartTotalElement = document.getElementById('cart-total');

    const cart = cartAPI.getCart();

    if (cart.length === 0) {
        cartItemsContainer.innerHTML = '<p>سبد خرید شما خالی است!</p>';
        cartTotalElement.textContent = '0';
        return;
    }

    // نمایش آیتم‌های سبد
    cartItemsContainer.innerHTML = cart.map(item => {
        const imagePath = item.image?.path
            ? item.image.path
            : item.image
                ? `/images/${item.image}`
                : 'https://via.placeholder.com/100';
        const title = item.title || item.name || '';
        return `
            <div class="row mb-3 align-items-center">
                <div class="col-md-2">
                    <img src="${imagePath}" class="img-fluid" alt="${title}">
                </div>
                <div class="col-md-4">
                    <h5>${title}</h5>
                    <p>${item.price} تومان</p>
                </div>
                <div class="col-md-3">
                    <div class="input-group">
                        <button class="btn btn-outline-secondary" onclick="updateQuantity('${item.id}', -1)">-</button>
                        <input type="text" class="form-control text-center" value="${item.quantity}" readonly>
                        <button class="btn btn-outline-secondary" onclick="updateQuantity('${item.id}', 1)">+</button>
                    </div>
                </div>
                <div class="col-md-2">
                    <h5>${item.price * item.quantity} تومان</h5>
                </div>
                <div class="col-md-1">
                    <button class="btn btn-danger" onclick="removeFromCart('${item.id}')">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        `;
    }).join('');

    // جمع کل
    const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    cartTotalElement.textContent = total;
});

// به‌روز کردن تعداد کالا
window.updateQuantity = function(productId, change) {
    const cart = cartAPI.getCart();
    const item = cart.find(item => item.id === productId || item._id === productId);
    if (item) {
        item.quantity += change;
        if (item.quantity <= 0) {
            cartAPI.removeFromCart(productId);
        } else {
            localStorage.setItem('cart', JSON.stringify(cart));
        }
        location.reload();
    }
};

// حذف آیتم از سبد
window.removeFromCart = function(productId) {
    cartAPI.removeFromCart(productId);
    location.reload();
};

// تکمیل خرید (ثبت سفارش)
document.getElementById('checkoutBtn')?.addEventListener('click', async () => {
    const cart = cartAPI.getCart();
    if (cart.length === 0) {
        alert('سبد خرید شما خالی است!');
        return;
    }
    const token = localStorage.getItem('token');
    if (!token) {
        alert('برای ثبت سفارش لطفاً وارد حساب کاربری خود شوید.');
        window.location.href = '/auth.html';
        return;
    }
    try {
        // آماده‌سازی داده‌های سفارش: فیلدهای مورد نیاز ممکن است متفاوت باشند
        const items = cart.map(item => ({
            productId: item.id,
            quantity: item.quantity
        }));
        await orderAPI.createOrder({ items }, token);
        alert('سفارش شما ثبت شد!');
        // پاک‌سازی سبد و هدایت به صفحه سفارش‌ها یا صفحه اصلی
        localStorage.removeItem('cart');
        window.location.href = '/orders.html';
    } catch (error) {
        console.error('Checkout error:', error);
        alert('خطا در ثبت سفارش!');
    }
});
