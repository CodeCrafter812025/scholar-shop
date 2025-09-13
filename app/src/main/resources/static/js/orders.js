import { orderAPI } from './api.js';

document.addEventListener('DOMContentLoaded', async () => {
    const ordersContainer = document.getElementById('orders-container');
    const token = localStorage.getItem('token');

    if (!token) {
        ordersContainer.innerHTML = '<p>لطفاً ابتدا وارد شوید!</p>';
        return;
    }

    try {
        const orders = await orderAPI.getUserOrders(token);

        if (orders.length === 0) {
            ordersContainer.innerHTML = '<p>هیچ سفارشی یافت نشد!</p>';
            return;
        }

        ordersContainer.innerHTML = orders.map(order => `
            <div class="card mb-3">
                <div class="card-header d-flex justify-content-between">
                    <span>سفارش #${order._id}</span>
                    <span class="badge bg-${order.status === 'completed' ? 'success' : 'warning'}">
                        ${order.status === 'completed' ? 'تکمیل شده' : 'در حال پردازش'}
                    </span>
                </div>
                <div class="card-body">
                    <h5>تاریخ: ${new Date(order.createdAt).toLocaleDateString('fa-IR')}</h5>
                    <h6>محصولات:</h6>
                    <ul>
                        ${order.products.map(item => `
                            <li>${item.product.name} - تعداد: ${item.quantity} - قیمت: ${item.price} تومان</li>
                        `).join('')}
                    </ul>
                    <h5 class="text-primary">جمع کل: ${order.totalAmount} تومان</h5>
                </div>
            </div>
        `).join('');
    } catch (error) {
        ordersContainer.innerHTML = '<p>خطا در دریافت سفارش‌ها!</p>';
        console.error(error);
    }
});

// خروج کاربر
document.getElementById('logout-btn')?.addEventListener('click', () => {
    localStorage.removeItem('token');
    window.location.href = 'index.html';
});